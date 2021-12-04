package de.skyslycer.hmclink.plugin

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.caching.user.UserCache
import de.skyslycer.hmclink.plugin.caching.user.UserCacheUpdater
import de.skyslycer.hmclink.plugin.caching.voice.VoiceCache
import de.skyslycer.hmclink.plugin.caching.voice.VoiceCacheUpdater
import de.skyslycer.hmclink.plugin.commands.CommandRegister
import de.skyslycer.hmclink.plugin.listeners.PlayerJoinListener
import de.skyslycer.hmclink.plugin.messaging.*
import de.skyslycer.hmclink.plugin.rewards.RewardProcessor
import de.skyslycer.hmclink.plugin.utils.ErrorUtilities
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.main.KSpigot
import net.axay.kspigot.runnables.firstAsync
import net.axay.kspigot.runnables.thenSync
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

@ExperimentalSerializationApi
class HMCLinkPlugin : KSpigot() {

    private lateinit var configuration: FileConfiguration

    private val rewardProcessor = RewardProcessor()
    private val userCache = UserCache()
    private val voiceCache = VoiceCache()

    private var messageHandler: Optional<MessageHandler> = Optional.empty()

    /**
     * The startup sequence of the plugin.
     */
    override fun startup() {
        Bukkit.getLogger().info(
            Constants.STARTUP_MESSAGE
        )

        try {
            configuration = YamlConfiguration.loadConfiguration(Constants.CONFIG_FILE.toFile())
        } catch (exception: Throwable) {
            ErrorUtilities.sendError(ErrorUtilities.SpecificException(exception, "config loading"))
            Constants.PLUGIN_MANAGER.disablePlugin(this)
            return
        }

        startupChecks()
    }

    /**
     * The shutdown sequence of the plugin.
     */
    override fun shutdown() {
        Bukkit.getLogger().info(
            Constants.SHUTDOWN_MESSAGE
        )

        messageHandler.ifPresent {
            it.jedis.shutdown()
        }
    }

    private fun startupChecks() {
        firstAsync {
            var distributor: Optional<MessageDistributor> = Optional.empty()

            val handler = MessageHandler(
                ServiceType.MINECRAFT_PLUGIN,
                configuration.getString("connection.host", "localhost")!!,
                configuration.getInt("connection.host", 6379)
            )

            val connectionResponse = handler.createJedis()

            if (connectionResponse.isPresent) {
                ErrorUtilities.sendError(ErrorUtilities.SpecificException(connectionResponse.get(), "Redis connection"))
            } else {
                messageHandler = Optional.of(handler)
                distributor = Optional.of(MessageDistributor(handler))
            }

            distributor
        }.thenSync {
            if (it.isPresent && !checkDependencies()) {
                CommandRegister(it.get())
                PlayerJoinListener(messageHandler.get())

                rewardProcessor.loadAll(configuration)

                UserCacheUpdater(userCache, messageHandler.get(), configuration)
                VoiceCacheUpdater(voiceCache, messageHandler.get(), configuration)

                setupMessageReceivers(it.get())
            } else {
                Constants.PLUGIN_MANAGER.disablePlugin(this)
            }
        }
    }

    private fun setupMessageReceivers(distributor: MessageDistributor) {
        val linkReceiver = LinkMessageReceiver(messageHandler.get(), rewardProcessor)
        val unlinkReceiver = UnlinkMessageReceiver(messageHandler.get(), rewardProcessor)
        LinkedUserUpdateReceiver(distributor, userCache)
        VoiceChannelUpdateReceiver(distributor, voiceCache)

        WrappedMessageReceiver(distributor, linkReceiver, unlinkReceiver)
        WaitingMessagesReceiver(distributor, linkReceiver, unlinkReceiver)
    }

    private fun checkDependencies(): Boolean {
        if (checkDependency("PlaceholderAPI")) {
            return true
        }

        return false
    }

    private fun checkDependency(name: String): Boolean {
        if (!Constants.PLUGIN_MANAGER.isPluginEnabled(name)) {
            Constants.CONSOLE.sendMessage(
                MiniMessage.get().parse(String.format(Constants.MISSING_PLUGIN_MESSAGE, name))
            )
            return true
        }

        return false
    }

}