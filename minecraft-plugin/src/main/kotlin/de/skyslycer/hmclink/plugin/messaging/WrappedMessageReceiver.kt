package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.messages.minecraft.IdentifiableMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkExecutorAnswerMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class WrappedMessageReceiver(
    private val distributor: MessageDistributor,
    private val linkReceiver: LinkMessageReceiver,
    private val unlinkReceiver: UnlinkMessageReceiver
) : MessageReceiver {

    init {
        setup()
    }

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<IdentifiableMessage>({
            val message = it as IdentifiableMessage

            handleReceive(message.message, message.code)
        })
    }

    private fun handleReceive(message: Message, code: String) {
        (message as? LinkAnswerMessage)?.let { linkReceiver.handleLinkReceive(it, code) }
        (message as? LinkSuccessMessage)?.let { linkReceiver.handleLinkSuccess(it, code) }
        (message as? LinkErrorMessage)?.let { linkReceiver.handleLinkError(it, code) }
        (message as? UnlinkAnswerMessage)?.let { unlinkReceiver.handleReceive(it, code) }
        (message as? UnlinkExecutorAnswerMessage)?.let { unlinkReceiver.handleExecutorReceive(it, code) }
    }

}