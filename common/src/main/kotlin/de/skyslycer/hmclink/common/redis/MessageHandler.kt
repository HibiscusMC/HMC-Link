package de.skyslycer.hmclink.common.redis

import de.skyslycer.hmclink.common.messages.checks.AliveMessage
import kotlinx.serialization.ExperimentalSerializationApi
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.Jedis
import java.util.*

@ExperimentalSerializationApi
class MessageHandler(val serviceType: String, private val host: String, private val port: Int) {

    lateinit var jedis: Jedis
    lateinit var pubSubHelper: PubSubHelper

    val onlineServices = HashSet<String>()

    /**
     * Try to connect to Redis.
     *
     * @return The exception when applicable
     */
    @Throws(Throwable::class)
    fun createJedis(): Optional<Throwable> {
        return try {
            jedis = Jedis(
                HostAndPort(host, port)
            )

            setupSubscriber()

            Optional.empty()
        } catch (exception: Throwable) {
            Optional.of(exception)
        }
    }

    private fun setupSubscriber() {
        pubSubHelper = PubSubHelper(jedis)

        aliveListener()
    }

    private fun aliveListener() {
        pubSubHelper.listenSolo(Channels.ALIVE) { message ->
            if (message is AliveMessage && message.to == serviceType) {
                when (message.mode) {
                    AliveMessage.AliveMode.REQUEST -> {
                        pubSubHelper.publish(
                            Channels.ALIVE,
                            AliveMessage(serviceType, message.from, AliveMessage.AliveMode.ANSWER)
                        )
                    }
                    AliveMessage.AliveMode.ANSWER -> onlineServices.add(message.from)
                }
            }
        }
    }

}