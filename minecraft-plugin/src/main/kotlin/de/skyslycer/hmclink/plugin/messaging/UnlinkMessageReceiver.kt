package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.plugin.config.MessageHelper
import de.skyslycer.hmclink.plugin.queue.MessageQueue
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
@ExperimentalTime
class UnlinkMessageReceiver(
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
        distributor.add<UnlinkAnswerMessage> ({ handleReceive(it as UnlinkAnswerMessage) })
    }

    private fun handleReceive(message: UnlinkAnswerMessage) {
        Bukkit.getPlayer(message.player)?.sendMessage(MessageHelper.buildUnlinkReceiveMessage(message)) ?: kotlin.run {
            queue[message.player] = message
            return
        }
    }

}