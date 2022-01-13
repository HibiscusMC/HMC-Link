package de.skyslycer.hmclink.plugin.caching.user

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.updates.LinkedUserRequestMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.MessageHandler
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.configuration.file.FileConfiguration

@ExperimentalSerializationApi
class UserCacheUpdater(
    private val userCache: UserCache,
    private val messageHandler: MessageHandler,
    private val config: FileConfiguration
) {

    init {
        setup()
    }

    private val updateScope = CoroutineScope(Dispatchers.Default)

    private fun setup() {
        updateScope.launch { start() }
    }

    private suspend fun start() {
        task()
        delay(1000)
    }

    private fun task() {
        userCache.forEach { player, data ->
            if (data.added + 1000 * 60 * config.getInt("updater.user-cache", 5) <= System.currentTimeMillis()) {
                messageHandler.pubSubHelper.publish(
                    Channels.STANDARD,
                    LinkedUserRequestMessage(ServiceType.MINECRAFT_PLUGIN, ServiceType.BACKEND, listOf(player))
                )
            }
        }
    }

}