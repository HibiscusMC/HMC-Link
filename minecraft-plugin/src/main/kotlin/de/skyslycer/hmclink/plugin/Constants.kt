package de.skyslycer.hmclink.plugin

import de.skyslycer.hmclink.plugin.Constants.Companion.PLUGIN_VERSION
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import java.io.BufferedReader
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class Constants {

    companion object {
        val CONSOLE = Bukkit.getConsoleSender()
        val SERVER = Bukkit.getServer()
        val SERVICE_MANAGER = Bukkit.getServicesManager()
        val PLUGIN_MANAGER = SERVER.pluginManager

        val PLUGIN_PATH = Paths.get("plugins", "ItemEconomy")
        val LOG_DIRECTORY = Paths.get("logs")

        val CONFIG_FILE = PLUGIN_PATH.resolve("config.yml")

        val MESSAGE_FILE = PLUGIN_PATH.resolve("messages.properties")

        val PLUGIN_VERSION: String = try {
            BufferedReader(
                HMCLinkPlugin::class.java.classLoader.getResourceAsStream("version")!!.reader()
            ).lines().findFirst().get()
        } catch (exception: Throwable) {
            "unspecified"
        }

        val STARTUP_MESSAGE = '\n' + """
            ====================
            > <bold><gradient:#64c400:#f0c400>ItemEconomy</gradient><reset><white> <italic>v${PLUGIN_VERSION}</italic> by <gradient:#64c400:#f0c400>Skyslycer</gradient> is enabling!
            ====================
        """.trimIndent()

        val SHUTDOWN_MESSAGE = '\n' + """
            ====================
            > <bold><gradient:#64c400:#f0c400>ItemEconomy</gradient><reset><white> <italic>v${PLUGIN_VERSION}</italic> by <gradient:#64c400:#f0c400>Skyslycer</gradient> is disabling!
            ====================
        """.trimIndent()

        val ERROR_MESSAGE = '\n' + """
            ====================
            > <bold><gradient:#64c400:#f0c400>ItemEconomy</gradient><reset><white> <italic>v${PLUGIN_VERSION}</italic> > A <gradient:#ff0000:#1204ff>%s</gradient> occurred!
            > <bold><gradient:#64c400:#f0c400>Error</gradient><reset><white> > %s
            > <bold><gradient:#64c400:#f0c400>Full log</gradient><reset><white> > %s
            ====================
        """.trimIndent()

        val MISSING_PLUGIN_MESSAGE = '\n' + """
            ====================
            > <bold><gradient:#64c400:#f0c400>ItemEconomy</gradient><reset><white> <italic>v${PLUGIN_VERSION}</italic> > Missing plugin: <gradient:#ff0000:#1204ff>%s</gradient>!
            ====================
        """.trimIndent()
    }

}