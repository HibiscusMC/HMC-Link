package de.skyslycer.hmclink.common.messages.minecraft

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class IdentifiableResponseMessage(
    override val from: String,
    override val to: String,
    val messages: Map<String, Message>
) : Message()