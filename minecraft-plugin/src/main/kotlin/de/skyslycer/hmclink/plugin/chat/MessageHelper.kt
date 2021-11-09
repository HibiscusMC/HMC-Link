package de.skyslycer.hmclink.plugin.chat

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkExecutorAnswerMessage
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MessageHelper {

    companion object {
        /**
         * Build the link answer message.
         *
         * @param message The message to get the data from
         * @return The translated and parsed message
         */
        fun buildLinkReceiveMessage(message: LinkAnswerMessage) = if (message.code == null) {
            ChatMessageHandler.getParsed(Messages.ALREADY_LINKED)
        } else {
            ChatMessageHandler.getParsed(Messages.LINK_RECEIVED, Replacement("link", message.code!!.link))
        }

        /**
         * Build the link success message.
         *
         * @param message The message to get the data from
         * @return The translated and parsed message
         */
        fun buildLinkSuccessMessage(message: LinkSuccessMessage) =
            ChatMessageHandler.getParsed(Messages.LINK_SUCCESS, Replacement("discord", message.discordName))

        /**
         * Build the unlink answer message.
         *
         * @param message The message to get the data from
         * @return The translated and parsed message
         */
        fun buildUnlinkReceiveMessage(message: UnlinkAnswerMessage) = if (message.successful) {
            ChatMessageHandler.getParsed(Messages.UNLINK_SUCCESS)
        } else {
            ChatMessageHandler.getParsed(Messages.NOT_LINKED)
        }

        /**
         * Build the unlink answer message for the executor.
         *
         * @param message The message to get the data from
         * @return The translated and parsed message
         */
        fun buildUnlinkExecutorReceiveMessage(message: UnlinkExecutorAnswerMessage) = if (message.successful) {
            ChatMessageHandler.getParsed(Messages.EXECUTOR_UNLINK_SUCCESS, Replacement("player", message.playerName))
        } else {
            ChatMessageHandler.getParsed(Messages.EXECUTOR_NOT_LINKED, Replacement("player", message.playerName))
        }

        /**
         * Build the link error message.
         *
         * @return The translated and parsed message
         */
        fun buildLinkErrorMessage() = ChatMessageHandler.getParsed(Messages.LINK_ERROR)
    }

}