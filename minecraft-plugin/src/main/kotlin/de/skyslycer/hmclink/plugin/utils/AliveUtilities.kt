package de.skyslycer.hmclink.plugin.utils

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.checks.AliveMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.chat.Messages
import de.skyslycer.hmclink.plugin.chat.Replacement
import de.skyslycer.hmclink.plugin.chat.sendParsedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class AliveUtilities {

    @ExperimentalSerializationApi
    companion object {
        /**
         * Wait for the backend to respond and execute an executor if the backend responds.
         *
         * @param time The time in seconds to wait for a response from the backend
         * @param distributor The distributor
         * @param scope The scope to wait in
         * @param player The player for the executor
         * @param executor The executor that is executed when the backend responds
         */
        fun onAliveInTime(time: Int, distributor: MessageDistributor, scope: CoroutineScope, player: Player, executor: (Player) -> Unit) {
            scope.launch {
                var alive = false

                distributor.messageHandler.pubSubHelper.publish(
                    Channels.ALIVE,
                    AliveMessage(
                        distributor.serviceType,
                        ServiceType.BACKEND,
                        AliveMessage.AliveMode.REQUEST
                    )
                )

                distributor.add<AliveMessage>({
                    val message = it as AliveMessage

                    if (message.to == distributor.serviceType && message.from == ServiceType.BACKEND && message.mode == AliveMessage.AliveMode.ANSWER) {
                        alive = true

                        executor.invoke(player)
                    }
                }, true)

                delay(time * 1000.toLong())

                if (!alive) {
                    player.sendParsedMessage(Messages.SERVICE_UNAVAILABLE, Replacement("service", ServiceType.BACKEND))
                }
            }
        }
    }

}