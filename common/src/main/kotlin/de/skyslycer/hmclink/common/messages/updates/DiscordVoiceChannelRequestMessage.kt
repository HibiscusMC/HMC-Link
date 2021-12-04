package de.skyslycer.hmclink.common.messages.updates

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
class DiscordVoiceChannelRequestMessage(
    override val from: String,
    override val to: String,
    val users: List<@Contextual Long>
) : Message()