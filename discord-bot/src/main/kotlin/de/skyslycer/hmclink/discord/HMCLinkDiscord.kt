package de.skyslycer.hmclink.discord

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.discord.messaging.DiscordVoiceChannelRequestReceiver
import de.skyslycer.hmclink.discord.messaging.UnlinkMessageReceiver
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging

@ExperimentalSerializationApi
suspend fun main() {
    HMCLinkDiscord().start()
}

@ExperimentalSerializationApi
class HMCLinkDiscord {

    private val logger = KotlinLogging.logger { }

    private val handler = MessageHandler(
        ServiceType.DISCORD_BOT,
        System.getenv(EnvironmentVariables.REDIS_HOST),
        System.getenv(EnvironmentVariables.REDIS_PORT).toInt()
    )

    /**
     * A basic method to start the Discord bot.
     */
    suspend fun start() {
        logger.info("Starting the HMCLink Discord bot...")

        val kord = try {
            Kord(System.getenv(EnvironmentVariables.BOT_TOKEN))
        } catch (exception: Throwable) {
            logger.error("An error occurred while trying to connect to the Discord API!", exception)
            return
        }

        val handlerReturn = handler.createJedis()

        if (handlerReturn.isPresent) {
            logger.error("An error occurred while trying to connect to Redis!", handlerReturn.get())
            return
        }

        val distributor = MessageDistributor(handler)

        UnlinkMessageReceiver(distributor, kord)
        DiscordVoiceChannelRequestReceiver(distributor, kord)

        logger.info("Successfully started the Discord bot!")

        kord.login {
            intents = Intents {
                +Intent.GuildInvites
                +Intent.GuildVoiceStates
            }
        }
    }

}