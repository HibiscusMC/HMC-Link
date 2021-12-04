package de.skyslycer.hmclink.common.messages.updates

import de.skyslycer.hmclink.common.data.User
import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalSerializationApi
@Serializable
class LinkedUserUpdateMessage(
    override val from: String,
    override val to: String,
    @Contextual val users: List<User>
) : Message()