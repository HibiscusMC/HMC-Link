package de.skyslycer.hmclink.backend.database

import java.util.*

data class DatabaseUser(
    val playerUUID: UUID,
    val playerName: String,
    var discordID: Optional<Long>,
    var discordName: Optional<String>,
    var code: Optional<String>,
    var linked: Boolean,
    var everLinked: Boolean
)