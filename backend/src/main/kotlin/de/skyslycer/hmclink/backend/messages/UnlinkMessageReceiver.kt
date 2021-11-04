package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.database.tables.DiscordMessageTable
import de.skyslycer.hmclink.backend.utils.AliveUtilities
import de.skyslycer.hmclink.backend.utils.PluginCommunicationUtilities
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.discord.LinkRemoveMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkExecutorAnswerMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

@ExperimentalSerializationApi
class UnlinkMessageReceiver(
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
        distributor.add<UnlinkRequestMessage>({ handleReceive(it as UnlinkRequestMessage) })
    }

    private fun handleReceive(message: UnlinkRequestMessage) {
        logger.info("Received unlink request message! Removing code and notifying the Discord bot. (UUID: ${message.player})")

        scope.launch {
            val user = DatabaseHandler.get(message.player)

            if (user.isPresent && !user.get().linked) {
                sendAnswer(message, false)
                return@launch
            }

            if (user.isPresent) {
                user.get().discordID.ifPresent(this@UnlinkMessageReceiver::sendLinkRemove)

                DatabaseHandler.update(
                    message.player,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    false,
                    user.get().everLinked
                )
            }

            sendAnswer(message, true)
        }
    }

    private suspend fun sendAnswer(message: UnlinkRequestMessage, successful: Boolean) {
        PluginCommunicationUtilities.send(
            distributor.messageHandler,
            UnlinkAnswerMessage(message.to, message.from, message.player, message.playerName, successful)
        )

        if (message.executor != null) {
            PluginCommunicationUtilities.send(
                distributor.messageHandler,
                UnlinkExecutorAnswerMessage(
                    message.to,
                    message.from,
                    message.playerName,
                    message.executor!!,
                    message.executorName!!,
                    successful
                )
            )
        }
    }

    private fun sendLinkRemove(id: Long) {
        val message = LinkRemoveMessage(
            ServiceType.BACKEND,
            ServiceType.DISCORD_BOT,
            id
        )

        AliveUtilities.onAliveInTime(3, distributor, scope, message, {
            distributor.messageHandler.pubSubHelper.publish(
                Channels.STANDARD,
                it
            )
        }) {
            newSuspendedTransaction {
                DiscordMessageTable.insert {
                    it[this.message] = message.toByteArray()
                }
            }
        }
    }

}