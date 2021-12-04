package de.skyslycer.hmclink.common.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    @Contextual val playerUUID: UUID,
    val playerName: String,
    var discordID: Long?,
    var discordName: String?,
    var code: String?,
    var linked: Boolean,
    var everLinked: Boolean
)