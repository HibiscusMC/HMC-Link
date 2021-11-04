package de.skyslycer.hmclink.common.redis.receiving

import de.skyslycer.hmclink.common.messages.Message
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.function.Predicate

@ExperimentalSerializationApi
class MessageDistributor(val messageHandler: MessageHandler) : MessageReceiver {

    init {
        setup()
    }

    val serviceType = messageHandler.serviceType

    val distributors = ArrayList<DistributorData>()

    /**
     * Add an executor to a message type.
     *
     * @param T The message type
     * @param executor The executor that runs, when all conditions are met
     * @param oneTime If the distributor should remove itself after one run
     */
    inline fun <reified T : Message> add(noinline executor: (Message) -> Unit, oneTime: Boolean = false): DistributorData {
        val data = DistributorData({ it is T }, executor, oneTime)

        distributors.add(data)

        return data
    }

    /**
     * Start the receiver/distributor.
     */
    override fun setup() {
        messageHandler.pubSubHelper.listen(Channels.STANDARD, Channels.ALIVE) { _, message ->
            if (message.to != serviceType && message.to != "*") return@listen

            distributors.forEach { data ->
                if (data.predicate.test(message)) {
                    data.executor.invoke(message)

                    if (data.oneTime) {
                        distributors.remove(data)
                    }
                }
            }
        }
    }

    data class DistributorData(
        val predicate: Predicate<Message>,
        val executor: (Message) -> Unit,
        val oneTime: Boolean
    )

}