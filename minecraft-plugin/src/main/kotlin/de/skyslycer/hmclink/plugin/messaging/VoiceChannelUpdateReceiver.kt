package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.updates.LinkedUserUpdateMessage
import de.skyslycer.hmclink.common.messages.updates.VoiceChannelUpdateMessage
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.plugin.caching.voice.VoiceCache
import de.skyslycer.hmclink.plugin.caching.voice.VoiceCacheEntry
import de.skyslycer.hmclink.plugin.utils.BackendCommunicationUtilities
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class VoiceChannelUpdateReceiver(
    private val distributor: MessageDistributor,
    private val cache: VoiceCache
) : MessageReceiver {

    init {
        setup()
    }

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<LinkedUserUpdateMessage>({ handleReceive(it as VoiceChannelUpdateMessage) })
    }

    private fun handleReceive(message: VoiceChannelUpdateMessage) {
        message.users.forEach {
            cache[it.key] = VoiceCacheEntry(System.currentTimeMillis(), it.value)
        }
    }

}