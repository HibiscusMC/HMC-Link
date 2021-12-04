package de.skyslycer.hmclink.common.messages.updates

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

@ExperimentalSerializationApi
@Serializable
class VoiceChannelRequestMessage(
    override val from: String,
    override val to: String,
    val users: List<@Contextual UUID>
) : Message()