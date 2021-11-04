package de.skyslycer.hmclink.plugin.listeners

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.minecraft.RequestIdentifiableMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.event.listen
import org.bukkit.event.player.PlayerJoinEvent

@ExperimentalSerializationApi
class PlayerJoinListener(
    private val handler: MessageHandler
) {

    init {
        listen()
    }

    private fun listen() {
        listen<PlayerJoinEvent> {
            handler.pubSubHelper.publish(
                Channels.STANDARD,
                RequestIdentifiableMessage(ServiceType.MINECRAFT_PLUGIN, ServiceType.BACKEND)
            )
        }
    }

}