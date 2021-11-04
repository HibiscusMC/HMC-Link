package de.skyslycer.hmclink.plugin.chat

import de.skyslycer.hmclink.plugin.Constants
import kotlinx.serialization.ExperimentalSerializationApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.nio.file.Files
import java.util.*

@ExperimentalSerializationApi
class ChatMessageHandler {

    companion object {
        private val bundle = PropertyResourceBundle(Files.newInputStream(Constants.MESSAGE_FILE))

        /**
         * Get a string from the message resource bundle.
         *
         * @param key The key to search for
         * @return The retrieved string
         */
        fun get(key: String): String = bundle.getString(key)

        /**
         * Get a message and replace the replacements.
         *
         * @param key The key to get the message from
         * @param replacements The replacements that should be applied to the message
         * @return The translated message
         */
        fun getTranslated(key: String, vararg replacements: Replacement): String = Replacement.replace(get(key), *replacements)

        /**
         * Get a message, replace the replacements and parse it with MiniMessage.
         *
         * @param key The key to get the message from
         * @param replacements The replacements that should be applied to the message
         * @return The translated and parsed message
         */
        fun getParsed(key: String, vararg replacements: Replacement): Component {
            val replacementsList = arrayListOf<Replacement>()

            replacementsList.addAll(replacements)

            replacementsList.add(Replacement("prefix", get("prefix")))

            return MiniMessage.get().parse(getTranslated(key, *replacementsList.toTypedArray()))
        }

        /**
         * Parse a message with MiniMessage.
         *
         * @param message The message to parse
         * @return The parsed message
         */
        fun getParsed(message: String): Component {
            return MiniMessage.get().parse(message)
        }
    }

}

/**
 * Get a message, replace the replacements and parse it with MiniMessage.
 *
 * @param key The key to get the message from
 * @param replacements The replacements that should be applied to the message
 * @return The translated and parsed message
 */
@ExperimentalSerializationApi
fun Player.sendParsedMessage(key: String, vararg replacements: Replacement) {
    this.sendMessage(ChatMessageHandler.getParsed(key, *replacements))
}