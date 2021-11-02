package de.skyslycer.hmclink.common.redis.receiving

import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

@ExperimentalSerializationApi
class MessageDistributor(val messageHandler: MessageHandler) : MessageReceiver {

    init {
        setup()
    }

    val serviceType = messageHandler.serviceType

    val distributors = ConcurrentHashMap<Predicate<Message>, Pair<(Message) -> Unit, Boolean>>()

    /**
     * Add an executor to a message type.
     *
     * @param T The message type
     * @param executor The executor that runs, when all conditions are met
     */
    inline fun <reified T : Message> add(noinline executor: (Message) -> Unit, oneTime: Boolean = false) {
        distributors[Predicate { it is T }] = Pair(executor, oneTime)
    }

    /**
     * Start the receiver/distributor.
     */
    override fun setup() {
        messageHandler.pubSubHelper.listen(Channels.STANDARD, Channels.ALIVE) { channel, message ->
            if (message.to != serviceType && message.to != "*") return@listen

            distributors.forEach { (predicate, data) ->
                if (predicate.test(message)) {
                    data.first.invoke(message)

                    if (data.second) {
                        distributors.remove(predicate, data)
                    }
                }
            }
        }
    }

}