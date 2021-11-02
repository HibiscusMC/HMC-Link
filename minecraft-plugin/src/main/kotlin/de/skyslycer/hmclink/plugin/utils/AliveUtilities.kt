package de.skyslycer.hmclink.plugin.utils

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.checks.AliveMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.config.Messages
import de.skyslycer.hmclink.plugin.config.Replacement
import de.skyslycer.hmclink.plugin.config.sendParsedMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class AliveUtilities {

    @ExperimentalTime
    @ExperimentalSerializationApi
    companion object {
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

                delay(Duration.seconds(time))

                if (!alive) {
                    player.sendParsedMessage(Messages.SERVICE_UNAVAILABLE, Replacement("service", ServiceType.BACKEND))
                }
            }
        }
    }

}