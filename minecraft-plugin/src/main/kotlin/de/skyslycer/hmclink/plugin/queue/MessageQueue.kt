package de.skyslycer.hmclink.plugin.queue

import de.skyslycer.hmclink.common.messages.Message
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@ExperimentalSerializationApi
class MessageQueue : Queue<Message>()