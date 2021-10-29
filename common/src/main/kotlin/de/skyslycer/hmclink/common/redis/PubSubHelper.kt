package de.skyslycer.hmclink.common.redis

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi
import redis.clients.jedis.BinaryJedisPubSub
import redis.clients.jedis.Jedis

@ExperimentalSerializationApi
class PubSubHelper(private val jedis: Jedis) {

    /**
     * Listen to channels.
     *
     * @param channels The channels to listen to
     * @param executor The executor that executes when a message is received
     */
    fun listen(vararg channels: String, executor: (String, Message) -> Unit) {
        jedis.subscribe(object : BinaryJedisPubSub() {
            override fun onMessage(channel: ByteArray, message: ByteArray) {
                val decodedMessage = Message.fromByteArray(message)
                val decodedChannel = String(channel)

                executor.invoke(decodedChannel, decodedMessage)
            }
        }, *channels.map { it.toByteArray() }.toTypedArray())
    }

    /**
     * Listen to a single channel.
     *
     * @param channel The channel to listen to
     * @param executor The executor that executes when a message is received
     */
    fun listenSolo(channel: String, executor: (Message) -> Unit) {
        listen(channel) { _, message ->
            executor.invoke(message)
        }
    }

    /**
     * Publish a message.
     *
     * @param channel The channel to send the message to
     * @param message The message to send
     */
    fun publish(channel: String, message: Message) {
        jedis.publish(channel.toByteArray(), message.toByteArray())
    }

}