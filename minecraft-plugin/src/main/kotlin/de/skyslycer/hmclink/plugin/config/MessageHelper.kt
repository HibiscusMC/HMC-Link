package de.skyslycer.hmclink.plugin.config

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
@ExperimentalTime
class MessageHelper {

    companion object {
        fun buildLinkReceiveMessage(message: LinkAnswerMessage) = if (message.code == null) {
            ChatMessageHandler.getParsed(Messages.ALREADY_LINKED)
        } else {
            ChatMessageHandler.getParsed(Messages.LINK_RECEIVED, Replacement("link", message.code!!.link))
        }

        fun buildLinkSuccessMessage(message: LinkSuccessMessage) =
            ChatMessageHandler.getParsed(Messages.LINK_SUCCESS, Replacement("account", message.discordName))

        fun buildUnlinkReceiveMessage(message: UnlinkAnswerMessage) = if (message.successful) {
            ChatMessageHandler.getParsed(Messages.UNLINK_SUCCESS)
        } else {
            ChatMessageHandler.getParsed(Messages.NOT_LINKED)
        }

        fun buildLinkErrorMessage() = ChatMessageHandler.getParsed(Messages.LINK_ERROR)
    }

}