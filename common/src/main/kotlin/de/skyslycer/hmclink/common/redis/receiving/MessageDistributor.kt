package de.skyslycer.hmclink.common.redis.receiving

import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

@ExperimentalSerializationApi
class MessageDistributor(private val messageHandler: MessageHandler, private val serviceType: String) :
    MessageReceiver {

    init {
        setup()
    }

    val distributors = ConcurrentHashMap<Predicate<Message>, (Message) -> Unit>()

    /**
     * Add an executor to a message type.
     *
     * @param T The message type
     * @param executor The executor that runs, when all conditions are met
     */
    inline fun <reified T : Message> add(noinline executor: (Message) -> Unit) {
        distributors[Predicate { it is T }] = executor
    }

    /**
     * Start the receiver/distributor.
     */
    override fun setup() {
        messageHandler.pubSubHelper.listenSolo(Channels.STANDARD) {
            if (it.to != serviceType && it.to != "*") return@listenSolo

            distributors.forEach { (predicate, executor) ->
                if (predicate.test(it)) {
                    executor.invoke(it)
                }
            }
        }
    }

}