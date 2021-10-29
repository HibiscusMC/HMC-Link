package de.skyslycer.hmclink.backend.http.payload

import kotlinx.serialization.Serializable

@Serializable
class GuildAddPayload(
    val accessToken: String,
    val nick: String,
    val roles: Array<Long>
)