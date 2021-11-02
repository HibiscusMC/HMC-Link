package de.skyslycer.hmclink.plugin

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.plugin.commands.CommandRegister
import de.skyslycer.hmclink.plugin.listeners.PlayerJoinListener
import de.skyslycer.hmclink.plugin.queue.MessageQueue
import de.skyslycer.hmclink.plugin.utils.ErrorUtilities
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.main.KSpigot
import net.axay.kspigot.runnables.firstAsync
import net.axay.kspigot.runnables.thenSync
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class HMCLinkPlugin : KSpigot() {

    private val messageQueue = MessageQueue()

    override fun startup() {
        Bukkit.getLogger().info(
            Constants.STARTUP_MESSAGE
        )

        firstAsync {
            // TODO: Add config n' stuff
            var distributor: Optional<MessageDistributor> = Optional.empty()

            try {
                val handler = MessageHandler(ServiceType.MINECRAFT_PLUGIN, "blahblub", 1337)
                distributor = Optional.of(MessageDistributor(handler))
            } catch (exception: Throwable) {
                ErrorUtilities.sendError(ErrorUtilities.SpecificException(exception, "Redis connection"))
            }

            distributor
        }.thenSync {
            if (it.isPresent) {
                CommandRegister(it.get())
                PlayerJoinListener(messageQueue)
            }
        }
    }

    override fun shutdown() {
        Bukkit.getLogger().info(
            Constants.SHUTDOWN_MESSAGE
        )
    }

}