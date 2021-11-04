package de.skyslycer.hmclink.common.messages.minecraft

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class RemoveIdentifiableMessage(
    override val from: String,
    override val to: String,
    val code: String
) : Message()