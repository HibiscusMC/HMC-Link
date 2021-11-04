package de.skyslycer.hmclink.plugin.utils

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.minecraft.RemoveIdentifiableMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class BackendCommunicationUtilities {

    companion object {
        /**
         * Send an acknowledgment message to the backend.
         *
         * @param handler The message handler
         * @param code The code of the message to acknowledge
         */
        fun sendAcknowledge(handler: MessageHandler, code: String) {
            handler.pubSubHelper.publish(
                Channels.STANDARD,
                RemoveIdentifiableMessage(ServiceType.MINECRAFT_PLUGIN, ServiceType.MINECRAFT_PLUGIN, code)
            )
        }
    }

}