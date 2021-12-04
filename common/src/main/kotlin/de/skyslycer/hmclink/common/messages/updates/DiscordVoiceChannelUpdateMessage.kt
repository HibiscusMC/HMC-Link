package de.skyslycer.hmclink.common.messages.updates

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
class DiscordVoiceChannelUpdateMessage(
    override val from: String,
    override val to: String,
    val users: HashMap<Long, String>
) : Message()