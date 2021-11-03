package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.plugin.chat.MessageHelper
import de.skyslycer.hmclink.plugin.queue.MessageQueue
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
class LinkMessageReceiver(
    private val distributor: MessageDistributor,
    private val queue: MessageQueue
) : MessageReceiver {

    init {
        setup()
    }

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<LinkAnswerMessage> ({ handleLinkReceive(it as LinkAnswerMessage) })
        distributor.add<LinkSuccessMessage> ({ handleLinkSuccess(it as LinkSuccessMessage) })
        distributor.add<LinkErrorMessage> ({ handleLinkError(it as LinkErrorMessage) })
    }

    private fun handleLinkReceive(message: LinkAnswerMessage) {
        Bukkit.getPlayer(message.player)?.sendMessage(MessageHelper.buildLinkReceiveMessage(message)) ?: kotlin.run {
            queue.add(message.player, message)
            return
        }
    }

    private fun handleLinkSuccess(message: LinkSuccessMessage) {
        Bukkit.getPlayer(message.player)?.sendMessage(MessageHelper.buildLinkSuccessMessage(message)) ?: kotlin.run {
            queue.add(message.player, message)
            return
        }
    }

    private fun handleLinkError(message: LinkErrorMessage) {
        Bukkit.getPlayer(message.player)?.sendMessage(MessageHelper.buildLinkErrorMessage()) ?: kotlin.run {
            queue.add(message.player, message)
            return
        }
    }

}