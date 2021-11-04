package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.tables.LinkTable
import de.skyslycer.hmclink.backend.database.tables.PluginMessageTable
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.messages.minecraft.IdentifiableResponseMessage
import de.skyslycer.hmclink.common.messages.minecraft.RemoveIdentifiableMessage
import de.skyslycer.hmclink.common.messages.minecraft.RequestIdentifiableMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@ExperimentalSerializationApi
class IdentifiableMessageReceiver(
    private val distributor: MessageDistributor
) : MessageReceiver {

    init {
        setup()
    }

    private val logger = KotlinLogging.logger { }

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<RequestIdentifiableMessage>({ handleRequest() })
        distributor.add<RemoveIdentifiableMessage>({ handleRemove(it as RemoveIdentifiableMessage) })
    }

    private fun handleRemove(message: RemoveIdentifiableMessage) {
        logger.info("Received remove request message! Deleting received code from database. (code: ${message.code})")

        scope.launch {
            newSuspendedTransaction {
                PluginMessageTable.deleteWhere {
                    LinkTable.code eq message.code
                }
            }
        }
    }

    private fun handleRequest() {
        logger.info("Received get request message! Sending saved data.")

        scope.launch {
            val messages = newSuspendedTransaction {
                PluginMessageTable.selectAll().mapNotNull {
                    it[PluginMessageTable.code] to Message.fromByteArray(it[PluginMessageTable.message])
                }.toMap()
            }

            distributor.messageHandler.pubSubHelper.publish(
                Channels.STANDARD,
                IdentifiableResponseMessage(ServiceType.BACKEND, ServiceType.MINECRAFT_PLUGIN, messages)
            )
        }
    }

}