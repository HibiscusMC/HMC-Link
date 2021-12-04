package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkExecutorAnswerMessage
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.plugin.chat.MessageHelper
import de.skyslycer.hmclink.plugin.rewards.RewardProcessor
import de.skyslycer.hmclink.plugin.rewards.RewardType
import de.skyslycer.hmclink.plugin.utils.BackendCommunicationUtilities
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit

@ExperimentalSerializationApi
class UnlinkMessageReceiver(
    private val handler: MessageHandler,
    private val rewardProcessor: RewardProcessor
) {

    fun handleReceive(message: UnlinkAnswerMessage, code: String) {
        Bukkit.getPlayer(message.player)?.let {
            it.sendMessage(MessageHelper.buildUnlinkReceiveMessage(message))
            rewardProcessor.process(RewardType.UNLINK, it)

            BackendCommunicationUtilities.sendAcknowledge(handler, code)
        }
    }

    fun handleExecutorReceive(message: UnlinkExecutorAnswerMessage, code: String) {
        Bukkit.getPlayer(message.executor)?.let {
            it.sendMessage(MessageHelper.buildUnlinkExecutorReceiveMessage(message))
            BackendCommunicationUtilities.sendAcknowledge(handler, code)
        }
    }

}