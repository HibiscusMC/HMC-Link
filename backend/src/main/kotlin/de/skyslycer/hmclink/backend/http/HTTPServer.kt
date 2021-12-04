package de.skyslycer.hmclink.backend.http

import de.skyslycer.hmclink.backend.EnvironmentVariables
import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.database.DatabaseUser
import de.skyslycer.hmclink.backend.http.payload.GuildAddPayload
import de.skyslycer.hmclink.backend.utils.DiscordUtilities
import de.skyslycer.hmclink.backend.utils.OAuthLinkGeneration
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import java.util.*

@ExperimentalSerializationApi
class HTTPServer(private val messageHandler: MessageHandler) {

    init {
        setup()
    }

    private val logger = KotlinLogging.logger { }

    private fun setup() {
        embeddedServer(CIO, port = 9348) {
            routing {
                get("/request") {
                    handleRequest(this.context)
                }

                get("/answer") {
                    handleAnswer(this.context)
                }
            }
        }.start(wait = true)
    }

    private suspend fun handleRequest(call: ApplicationCall) {
        val verificationCode = call.request.queryParameters["verification"]

        logger.info("Received HTTP link request call! Generating oAuth link... (code: $verificationCode)")

        if (verificationCode == null) {
            call.respondRedirect(System.getenv(EnvironmentVariables.REDIRECT_URL))
            return
        }

        call.respondRedirect(OAuthLinkGeneration.generateOAuthLink(verificationCode))
    }

    private suspend fun handleAnswer(call: ApplicationCall) {
        val oAuthCode = call.request.queryParameters["code"]
        val verificationCode = call.request.queryParameters["verification"]

        logger.info("Received HTTP oAuth response call! Setting up link... (code: $verificationCode, oAuth: $oAuthCode)")

        if (oAuthCode == null || verificationCode == null) {
            call.respondRedirect(System.getenv(EnvironmentVariables.INVALID_CODE_URL))
            return
        }

        val user = DatabaseHandler.get(verificationCode)

        if (user.isEmpty) {
            call.respondRedirect(System.getenv(EnvironmentVariables.INVALID_CODE_URL))
            return
        }

        val result = getDatabaseUser(call, oAuthCode, user.get())

        if (result.isEmpty) {
            sendErrorAnswer(user.get())
            call.respondRedirect(System.getenv(EnvironmentVariables.ERROR_REDIRECT_URL))
        } else {
            DatabaseHandler.update(result.get())
            sendSuccessAnswer(result.get())
            call.respondRedirect(System.getenv(EnvironmentVariables.SUCCESS_REDIRECT_URL))
        }
    }

    private fun sendErrorAnswer(user: DatabaseUser) {
        messageHandler.pubSubHelper.publish(
            Channels.STANDARD,
            LinkErrorMessage(
                ServiceType.BACKEND,
                ServiceType.MINECRAFT_PLUGIN,
                user.playerUUID,
                user.playerName,
            )
        )
    }

    private fun sendSuccessAnswer(result: DatabaseUser) {
        messageHandler.pubSubHelper.publish(
            Channels.STANDARD,
            LinkSuccessMessage(
                ServiceType.BACKEND,
                ServiceType.ALL,
                result.playerUUID,
                result.playerName,
                result.discordName.get()
            )
        )
    }

    private suspend fun getDatabaseUser(
        call: ApplicationCall,
        oAuthCode: String,
        user: DatabaseUser
    ): Optional<DatabaseUser> {
        val client = HttpClient(io.ktor.client.engine.cio.CIO)

        val response = DiscordRequests.getIdentity(oAuthCode, client)

        if (response.isEmpty) {
            call.respondRedirect(System.getenv(EnvironmentVariables.ERROR_REDIRECT_URL))
            return Optional.empty()
        }

        val payload = buildPayload(oAuthCode, response.get().username, user.playerName)

        val addToGuildResponse = DiscordRequests.addToGuild(response.get().id, payload, client)

        if (!addToGuildResponse) {
            call.respondRedirect(System.getenv(EnvironmentVariables.ERROR_REDIRECT_URL))
            return Optional.empty()
        }

        client.close()

        return Optional.of(buildNewUser(user, response.get().id.toLong(), response.get().username))
    }

    private fun buildNewUser(user: DatabaseUser, discordID: Long, discordName: String): DatabaseUser {
        return with(user) {
            this.code = Optional.empty()
            this.everLinked = true
            this.linked = true
            this.discordID = Optional.of(discordID)
            this.discordName =
                Optional.of(DiscordUtilities.getDiscordName(discordName, playerName))
            this
        }
    }

    private fun buildPayload(oAuthCode: String, discordName: String, playerName: String): GuildAddPayload {
        return GuildAddPayload(
            oAuthCode,
            DiscordUtilities.toNickName(discordName, playerName),
            arrayOf(System.getenv(EnvironmentVariables.LINKED_ROLE).toLong()).toLongArray()
        )
    }

}