package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.utils.CodeGeneration
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.data.Code
import de.skyslycer.hmclink.common.messages.discord.LinkRemoveMessage
import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkRequestMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import java.util.*

@ExperimentalSerializationApi
class UnlinkMessageReceiver(
    private val distributor: MessageDistributor,
    private val handler: MessageHandler
) : MessageReceiver {

    init {
        setup()
    }

    private val logger = KotlinLogging.logger {  }

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<UnlinkRequestMessage>({ handleReceive(it as UnlinkRequestMessage) })
    }

    private fun handleReceive(message: UnlinkRequestMessage) {
        logger.info("Received unlink request message! Removing code and notifying the Discord bot. (UUID: ${message.player})")

        scope.launch {
            val user = DatabaseHandler.get(message.player)

            if (user.isPresent && !user.get().linked) {
                sendAnswer(message, false)
                return@launch
            }

            if (user.isPresent) {
                user.get().discordID.ifPresent(this@UnlinkMessageReceiver::sendLinkRemove)

                DatabaseHandler.update(
                    message.player,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    false,
                    user.get().everLinked
                )
            }

            sendAnswer(message, true)
        }
    }

    private fun sendAnswer(message: UnlinkRequestMessage, successful: Boolean) {
        handler.pubSubHelper.publish(
            Channels.STANDARD,
            UnlinkAnswerMessage(message.to, message.from, message.player, message.playerName, message.executor, message.executorName, successful)
        )
    }

    private fun sendLinkRemove(id: Long) {
        handler.pubSubHelper.publish(
            Channels.STANDARD,
            LinkRemoveMessage(
                ServiceType.BACKEND,
                ServiceType.DISCORD_BOT,
                id
            )
        )
    }

}