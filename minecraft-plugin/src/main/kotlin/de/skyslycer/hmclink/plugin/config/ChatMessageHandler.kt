package de.skyslycer.hmclink.plugin.config

import de.skyslycer.hmclink.plugin.Constants
import kotlinx.serialization.ExperimentalSerializationApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class ChatMessageHandler {

    companion object {
        private val bundle = PropertyResourceBundle(Files.newInputStream(Constants.MESSAGE_FILE))

        fun get(key: String): String = bundle.getString(key)

        fun getTranslated(key: String, vararg replacements: Replacement): String = Replacement.replace(get(key), *replacements)

        fun getParsed(key: String, vararg replacements: Replacement): Component {
            val replacementsList = arrayListOf<Replacement>()

            replacementsList.addAll(replacements)

            replacementsList.add(Replacement("prefix", get("prefix")))

            return MiniMessage.get().parse(getTranslated(key, *replacementsList.toTypedArray()))
        }

        fun getParsed(message: String): Component {
            return MiniMessage.get().parse(message)
        }
    }

}

@ExperimentalSerializationApi
@ExperimentalTime
fun Player.sendParsedMessage(key: String, vararg replacements: Replacement) {
    this.sendMessage(ChatMessageHandler.getParsed(key, *replacements))
}