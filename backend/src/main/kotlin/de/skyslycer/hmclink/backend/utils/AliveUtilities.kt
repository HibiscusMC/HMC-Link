package de.skyslycer.hmclink.backend.utils

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.checks.AliveMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

class AliveUtilities {

    @ExperimentalSerializationApi
    companion object {
        /**
         * Wait for the backend to respond and execute an executor if the backend responds.
         *
         * @param T The type of the value getting passed in the executor
         * @param time The time in seconds to wait for a response from the backend
         * @param distributor The distributor
         * @param scope The scope to wait in
         * @param value The value for the executor
         * @param executor The executor that is executed when the discord bot responds
         * @param fallbackExecutor The executor that is executed when the discord doesn't respond
         */
        inline fun <reified T> onAliveInTime(
            time: Int,
            distributor: MessageDistributor,
            scope: CoroutineScope,
            value: T,
            crossinline executor: suspend (T) -> Unit,
            noinline fallbackExecutor: (suspend (T) -> Unit)? = null
        ) {
            scope.launch {
                var alive = false

                distributor.messageHandler.pubSubHelper.publish(
                    Channels.ALIVE,
                    AliveMessage(
                        distributor.serviceType,
                        ServiceType.DISCORD_BOT,
                        AliveMessage.AliveMode.REQUEST
                    )
                )

                val distributorData = distributor.add<AliveMessage>({
                    val aliveMessage = it as AliveMessage

                    if (aliveMessage.to == distributor.serviceType && aliveMessage.from == ServiceType.BACKEND && aliveMessage.mode == AliveMessage.AliveMode.ANSWER) {
                        alive = true

                        scope.launch {
                            executor.invoke(value)
                        }
                    }
                }, true)

                delay(time * 1000.toLong())

                if (!alive) {
                    distributor.distributors.remove(distributorData)
                    fallbackExecutor?.invoke(value)
                }
            }
        }
    }

}