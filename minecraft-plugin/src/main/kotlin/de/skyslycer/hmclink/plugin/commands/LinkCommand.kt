package de.skyslycer.hmclink.plugin.commands

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.link.LinkRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.utils.AliveUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.entity.Player
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class LinkCommand(
    private val scope: CoroutineScope,
    private val distributor: MessageDistributor
) {

    fun linkCommand(player: Player) {
        AliveUtilities.onAliveInTime(3, distributor, scope, player) {
            distributor.messageHandler.pubSubHelper.publish(
                Channels.STANDARD,
                LinkRequestMessage(
                    distributor.serviceType,
                    ServiceType.BACKEND,
                    player.uniqueId,
                    player.name
                )
            )
        }
    }

}