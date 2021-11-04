package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.messages.minecraft.IdentifiableResponseMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkExecutorAnswerMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class WaitingMessagesReceiver(
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
        distributor.add<IdentifiableResponseMessage>({ handleReceive(it as IdentifiableResponseMessage) })
    }

    private fun handleReceive(message: IdentifiableResponseMessage) {
        message.messages.forEach { (code, actualMessage) ->
            (actualMessage as? LinkAnswerMessage)?.let { linkReceiver.handleLinkReceive(it, code) }
            (actualMessage as? LinkSuccessMessage)?.let { linkReceiver.handleLinkSuccess(it, code) }
            (actualMessage as? LinkErrorMessage)?.let { linkReceiver.handleLinkError(it, code) }
            (actualMessage as? UnlinkAnswerMessage)?.let { unlinkReceiver.handleReceive(it, code) }
            (actualMessage as? UnlinkExecutorAnswerMessage)?.let { unlinkReceiver.handleExecutorReceive(it, code) }
        }
    }

}