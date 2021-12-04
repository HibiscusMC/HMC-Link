package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.database.DatabaseUser
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.updates.DiscordVoiceChannelRequestMessage
import de.skyslycer.hmclink.common.messages.updates.VoiceChannelRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging

@ExperimentalSerializationApi
class VoiceChannelRequestReceiver(
    private val distributor: MessageDistributor
) : MessageReceiver {

    init {
        setup()
    }

    private val logger = KotlinLogging.logger { }

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<VoiceChannelRequestMessage>({ handleRequest(it as VoiceChannelRequestMessage) })
    }

    private fun handleRequest(message: VoiceChannelRequestMessage) {
        logger.info("Received voice channel data request message! Forwarding onto Discord. (users: ${message.users})")

        scope.launch {
            val users = mutableListOf<DatabaseUser>()

            message.users.forEach { uuid ->
                DatabaseHandler.get(uuid).ifPresent { users.add(it) }
            }

            if (users.isNotEmpty()) {
                distributor.messageHandler.pubSubHelper.publish(
                    Channels.STANDARD,
                    DiscordVoiceChannelRequestMessage(
                        distributor.serviceType,
                        ServiceType.DISCORD_BOT,
                        users.mapNotNull { it.discordID.orElse(null) }
                    )
                )
            }
        }
    }

}