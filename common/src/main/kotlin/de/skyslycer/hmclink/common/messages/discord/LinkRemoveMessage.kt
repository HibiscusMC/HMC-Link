package de.skyslycer.hmclink.common.messages.discord

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class LinkRemoveMessage(
    override val from: String,
    override val to: String,
    val id: Long
) : Message()