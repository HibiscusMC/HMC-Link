package de.skyslycer.hmclink.backend.utils

import de.skyslycer.hmclink.backend.database.tables.PluginMessageTable
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.messages.minecraft.IdentifiableMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import org.apache.commons.lang3.RandomStringUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@ExperimentalSerializationApi
class BotCommunicationUtilities {

    companion object {
        /**
         * Send a message wrapped with a generated code to identify the message.
         *
         * @param handler The message handler
         * @param message The message to wrap
         */
        suspend fun send(handler: MessageHandler, message: Message) {
            val code = RandomStringUtils.randomAlphanumeric(64)

            handler.pubSubHelper.publish(
                Channels.STANDARD,
                IdentifiableMessage(ServiceType.BACKEND, ServiceType.MINECRAFT_PLUGIN, code, message)
            )

            newSuspendedTransaction {
                PluginMessageTable.insert {
                    it[this.code] = code
                    it[this.message] = message.toByteArray()
                }
            }
        }
    }

}