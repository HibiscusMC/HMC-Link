package de.skyslycer.hmclink.plugin.commands

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.unlink.UnlinkRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.chat.ChatMessageHandler
import de.skyslycer.hmclink.plugin.chat.Messages
import de.skyslycer.hmclink.plugin.utils.AliveUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

@ExperimentalSerializationApi
class UnlinkCommand(
    private val scope: CoroutineScope,
    private val distributor: MessageDistributor
) {

    /**
     * The function that should be ran when a player runs the unlink command.
     *
     * @param player The player that executes it
     * @param target The optional target. If empty the executing player is used
     */
    fun unlinkCommand(player: Player, target: Optional<String>) {
        AliveUtilities.onAliveInTime(3, distributor, scope, player) {
            var parsedTarget: OfflinePlayer? = null

            if (target.isPresent) {
                parsedTarget = Bukkit.getOfflinePlayerIfCached(target.get()) ?: return@onAliveInTime kotlin.run {
                    player.sendMessage(ChatMessageHandler.getParsed(Messages.INVALID_PLAYER))
                }
            }

            distributor.messageHandler.pubSubHelper.publish(
                Channels.STANDARD,
                UnlinkRequestMessage(
                    distributor.serviceType,
                    ServiceType.BACKEND,
                    if (target.isPresent) parsedTarget!!.uniqueId else player.uniqueId,
                    if (target.isPresent) parsedTarget!!.name!! else player.name,
                    if (target.isPresent) player.uniqueId else null,
                    if (target.isPresent) player.name else null
                )
            )
        }
    }

}