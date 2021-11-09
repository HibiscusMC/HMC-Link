package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.plugin.chat.MessageHelper
import de.skyslycer.hmclink.plugin.chat.Replacement
import de.skyslycer.hmclink.plugin.rewards.RewardProcessor
import de.skyslycer.hmclink.plugin.rewards.RewardType
import de.skyslycer.hmclink.plugin.utils.BackendCommunicationUtilities
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit

@ExperimentalSerializationApi
class LinkMessageReceiver(
    private val handler: MessageHandler,
    private val rewardProcessor: RewardProcessor
) {

    fun handleLinkReceive(message: LinkAnswerMessage, code: String) {
        Bukkit.getPlayer(message.player)?.let {
            it.sendMessage(MessageHelper.buildLinkReceiveMessage(message))
            BackendCommunicationUtilities.sendAcknowledge(handler, code)
        }
    }

    fun handleLinkSuccess(message: LinkSuccessMessage, code: String) {
        Bukkit.getPlayer(message.player)?.let {
            it.sendMessage(MessageHelper.buildLinkSuccessMessage(message))
            rewardProcessor.process(RewardType.LINK, it, Replacement("discord", message.discordName))

            BackendCommunicationUtilities.sendAcknowledge(handler, code)
        }
    }

    fun handleLinkError(message: LinkErrorMessage, code: String) {
        Bukkit.getPlayer(message.player)?.let {
            it.sendMessage(MessageHelper.buildLinkErrorMessage())
            BackendCommunicationUtilities.sendAcknowledge(handler, code)
        }
    }

}