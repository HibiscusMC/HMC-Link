package de.skyslycer.hmclink.discord.listeners

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.updates.DiscordVoiceChannelUpdateMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.discord.utils.AliveUtils
import dev.kord.core.event.user.VoiceStateUpdateEvent
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class VoiceStateUpdateListener(
    private val distributor: MessageDistributor
) : Listener<VoiceStateUpdateEvent> {

    override suspend fun supply(event: VoiceStateUpdateEvent) {
        if (event.state.getChannelOrNull() != event.old?.getChannelOrNull()) {
            val currentChannel = event.state.getChannelOrNull()?.fetchChannelOrNull()?.name
            val users = HashMap<Long, String>()
            users[event.state.userId.value.toLong()] = currentChannel.toString()

            AliveUtils.sendOrSave(
                DiscordVoiceChannelUpdateMessage(
                    distributor.serviceType,
                    ServiceType.BACKEND,
                    users
                ),
                distributor
            )
        }
    }

}