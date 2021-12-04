package de.skyslycer.hmclink.plugin.messaging

import de.skyslycer.hmclink.common.messages.updates.LinkedUserUpdateMessage
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.plugin.caching.user.UserCache
import de.skyslycer.hmclink.plugin.caching.user.UserCacheEntry
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@ExperimentalSerializationApi
class LinkedUserUpdateReceiver(
    private val distributor: MessageDistributor,
    private val cache: UserCache
) : MessageReceiver {

    init {
        setup()
    }

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<LinkedUserUpdateMessage>({ handleReceive(it as LinkedUserUpdateMessage) })
    }

    private fun handleReceive(message: LinkedUserUpdateMessage) {
        message.users.forEach {
            cache[it.playerUUID] = UserCacheEntry(
                System.currentTimeMillis(),
                it.linked,
                Optional.ofNullable(it.discordID),
                Optional.ofNullable(it.discordName)
            )
        }
    }

}