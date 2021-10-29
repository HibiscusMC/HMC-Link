package de.skyslycer.hmclink.backend.http.payload

data class UserPayload(
    val id: String,
    val username: String,
    val discriminator: String
)