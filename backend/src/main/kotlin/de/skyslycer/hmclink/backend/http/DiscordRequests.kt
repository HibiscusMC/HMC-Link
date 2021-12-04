package de.skyslycer.hmclink.backend.http

import de.skyslycer.hmclink.backend.EnvironmentVariables
import de.skyslycer.hmclink.backend.http.payload.GuildAddPayload
import de.skyslycer.hmclink.backend.http.payload.UserPayload
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import java.util.*

class DiscordRequests {

    companion object {
        private val logger = KotlinLogging.logger { }

        private const val BASE_URL = "https://discord.com/api/9"

        /**
         * Add a Discord user to the specified guild.
         *
         * @param id The ID of the user
         * @param payload The payload to send along the request
         * @param client The client to send the request
         * @return If the request was successful
         */
        suspend fun addToGuild(id: String, payload: GuildAddPayload, client: HttpClient): Boolean {
            logger.info("Adding user to guild, renaming the member and giving it a role... (id: $id)")

            return try {
                client.post<HttpResponse>("$BASE_URL/guilds/${System.getenv(EnvironmentVariables.GUILD_ID)}/members/$id") {
                    header("Authorization", "Bot ${System.getenv(EnvironmentVariables.BOT_TOKEN)}")
                    contentType(ContentType.Application.Json)
                    body = payload
                }

                true
            } catch (exception: Throwable) {
                false
            }
        }

        /**
         * Get identity information about a Discord user.
         *
         * @param token The oAuth access token
         * @param client The client to send the request
         * @return The returned UserPayload if applicable
         */
        suspend fun getIdentity(token: String, client: HttpClient): Optional<UserPayload> {
            logger.info("Getting user information... (token: $token)")

            return try {
                Optional.of(client.get("$BASE_URL/users/@me") {
                    header("Authorization", "Bearer $token")
                })
            } catch (exception: Throwable) {
                Optional.empty()
            }
        }
    }

}