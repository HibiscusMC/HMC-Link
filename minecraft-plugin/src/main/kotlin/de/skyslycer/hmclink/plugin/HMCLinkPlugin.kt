package de.skyslycer.hmclink.plugin

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.commands.CommandRegister
import de.skyslycer.hmclink.plugin.listeners.PlayerJoinListener
import de.skyslycer.hmclink.plugin.messaging.LinkMessageReceiver
import de.skyslycer.hmclink.plugin.messaging.UnlinkMessageReceiver
import de.skyslycer.hmclink.plugin.queue.MessageQueue
import de.skyslycer.hmclink.plugin.utils.ErrorUtilities
import kotlinx.coroutines.handleCoroutineException
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.main.KSpigot
import net.axay.kspigot.runnables.firstAsync
import net.axay.kspigot.runnables.thenSync
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
class HMCLinkPlugin : KSpigot() {

    private lateinit var configuration: FileConfiguration

    private var messageHandler: Optional<MessageHandler> = Optional.empty()
    private val messageQueue = MessageQueue()

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

            try {
                val handler = MessageHandler(
                    ServiceType.MINECRAFT_PLUGIN,
                    configuration.getString("connection.host", "localhost")!!,
                    configuration.getInt("connection.host", 6379)
                )

                messageHandler = Optional.of(handler)
                distributor = Optional.of(MessageDistributor(handler))
            } catch (exception: Throwable) {
                ErrorUtilities.sendError(ErrorUtilities.SpecificException(exception, "Redis connection"))
            }

            distributor
        }.thenSync {
            if (it.isPresent && !checkDependencies()) {
                CommandRegister(it.get())
                PlayerJoinListener(messageQueue)

                LinkMessageReceiver(it.get(), messageQueue)
                UnlinkMessageReceiver(it.get(), messageQueue)
            } else {
                Constants.PLUGIN_MANAGER.disablePlugin(this)
            }
        }
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