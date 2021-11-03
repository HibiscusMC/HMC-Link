package de.skyslycer.hmclink.common.messages.unlink

import de.skyslycer.hmclink.common.data.Code
import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

@ExperimentalSerializationApi
@Serializable
class UnlinkAnswerMessage(
    override val from: String,
    override val to: String,
    @Contextual val player: UUID,
    val playerName: String,
    @Contextual val executor: UUID?,
    val executorName: String?,
    val successful: Boolean
) : Message()