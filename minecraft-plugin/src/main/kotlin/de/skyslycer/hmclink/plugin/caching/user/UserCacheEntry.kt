package de.skyslycer.hmclink.plugin.caching.user

import java.util.*

data class UserCacheEntry(
    val added: Long,
    val linked: Boolean,
    val discordId: Optional<Long>,
    val discordName: Optional<String>
)