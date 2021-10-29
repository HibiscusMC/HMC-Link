package de.skyslycer.hmclink.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Code(
    val code: String,
    val link: String
)