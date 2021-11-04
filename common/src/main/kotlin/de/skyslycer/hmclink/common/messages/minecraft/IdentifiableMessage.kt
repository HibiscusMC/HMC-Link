package de.skyslycer.hmclink.common.messages.minecraft

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
data class IdentifiableMessage(
    override val from: String,
    override val to: String,
    val code: String,
    val message: Message
) : Message()