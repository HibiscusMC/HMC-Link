package de.skyslycer.hmclink.common.messages.checks

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class AliveMessage(
    override val from: String,
    override val to: String,
    val mode: AliveMode
) : Message() {

    enum class AliveMode {

        REQUEST,
        ANSWER

    }

}