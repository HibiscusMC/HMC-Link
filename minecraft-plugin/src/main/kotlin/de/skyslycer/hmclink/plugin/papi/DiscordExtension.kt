package de.skyslycer.hmclink.plugin.papi

import de.skyslycer.hmclink.plugin.caching.user.UserCache
import de.skyslycer.hmclink.plugin.caching.voice.VoiceCache
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class DiscordExtension(
    private val userCache: UserCache,
    private val voiceCache: VoiceCache
) : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "discord"
    }

    override fun getAuthor(): String {
        return "HMCLink"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        return when(params) {
            "linked" -> {
                userCache[player.uniqueId]?.linked?.toString() ?: ""
            }
            "name" -> {
                userCache[player.uniqueId]?.discordName?.toString() ?: ""
            }
            "id" -> {
                userCache[player.uniqueId]?.discordId?.toString() ?: ""
            }
            "voice_channel" -> {
                voiceCache[player.uniqueId]?.channelName ?: ""
            }
            else -> null
        }
    }

}