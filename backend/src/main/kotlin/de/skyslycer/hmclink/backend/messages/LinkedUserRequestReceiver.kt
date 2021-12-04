package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.data.User
import de.skyslycer.hmclink.common.messages.updates.LinkedUserRequestMessage
import de.skyslycer.hmclink.common.messages.updates.LinkedUserUpdateMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging

@ExperimentalSerializationApi
class LinkedUserRequestReceiver(
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
        distributor.add<LinkedUserRequestMessage>({ handleRequest(it as LinkedUserRequestMessage) })
    }

    private fun handleRequest(message: LinkedUserRequestMessage) {
        logger.info("Received user data request message! Sending data from the given users. (users: ${message.users})")

        scope.launch {
            val users = mutableListOf<User>()

            message.users.forEach { uuid ->
                DatabaseHandler.get(uuid).ifPresent { users.add(DatabaseHandler.toUniversalUser(it)) }
            }

            if (users.isNotEmpty()) {
                distributor.messageHandler.pubSubHelper.publish(
                    Channels.STANDARD,
                    LinkedUserUpdateMessage(
                        distributor.serviceType,
                        ServiceType.MINECRAFT_PLUGIN,
                        users
                    )
                )
            }
        }
    }

}