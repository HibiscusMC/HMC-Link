package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.updates.DiscordVoiceChannelUpdateMessage
import de.skyslycer.hmclink.common.messages.updates.VoiceChannelUpdateMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import java.util.*

@ExperimentalSerializationApi
class DiscordVoiceChannelUpdateReceiver(
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
        distributor.add<DiscordVoiceChannelUpdateMessage>({ handleRequest(it as DiscordVoiceChannelUpdateMessage) })
    }

    private fun handleRequest(message: DiscordVoiceChannelUpdateMessage) {
        logger.info("Received voice channel data update message! Forwarding onto Minecraft. (users: ${message.users})")

        scope.launch {
            val users = HashMap<UUID, String>()

            message.users.forEach { id ->
                DatabaseHandler.get(id.key).ifPresent { users[it.playerUUID] = id.value }
            }

            if (users.isNotEmpty()) {
                distributor.messageHandler.pubSubHelper.publish(
                    Channels.STANDARD,
                    VoiceChannelUpdateMessage(
                        distributor.serviceType,
                        ServiceType.DISCORD_BOT,
                        users
                    )
                )
            }
        }
    }

}