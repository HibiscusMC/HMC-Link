package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.utils.CodeGeneration
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.data.Code
import de.skyslycer.hmclink.common.messages.discord.LinkRemoveMessage
import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkRequestMessage
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
class LinkMessageReceiver(
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
        distributor.add<LinkRequestMessage>({ handleReceive(it as LinkRequestMessage) })
    }

    private fun handleReceive(message: LinkRequestMessage) {
        logger.info("Received link request message! Generating new code and inserting it. (UUID: ${message.player})")

        scope.launch {
            val user = DatabaseHandler.get(message.player)

            if (user.isPresent && user.get().linked) {
                sendAnswer(message, null)
                return@launch
            }

            val newCode = CodeGeneration.generateCode()

            if (user.isPresent) {
                user.get().discordID.ifPresent(this@LinkMessageReceiver::sendLinkRemove)

                DatabaseHandler.update(
                    message.player,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(newCode),
                    false,
                    user.get().everLinked
                )
            } else {
                DatabaseHandler.insert(
                    message.player,
                    message.playerName,
                    newCode
                )
            }
            sendAnswer(message, Code(newCode, CodeGeneration.generateLink(newCode)))
        }
    }

    private fun sendAnswer(message: LinkRequestMessage, code: Code?) {
        handler.pubSubHelper.publish(
            Channels.STANDARD,
            LinkAnswerMessage(message.to, message.from, message.player, message.playerName, code)
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