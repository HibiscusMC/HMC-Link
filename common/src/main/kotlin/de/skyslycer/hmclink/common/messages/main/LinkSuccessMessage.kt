package de.skyslycer.hmclink.common.messages.main

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*

@ExperimentalSerializationApi
@Serializable
class LinkSuccessMessage(
    override val from: String,
    override val to: String,
    @Contextual val player: UUID,
    val playerName: String,
    val discordName: String
) : Message()