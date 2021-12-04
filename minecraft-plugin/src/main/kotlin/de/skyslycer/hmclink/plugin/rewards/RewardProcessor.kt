package de.skyslycer.hmclink.plugin.rewards

import de.skyslycer.hmclink.plugin.Constants
import de.skyslycer.hmclink.plugin.chat.Replacement
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.extensions.bukkit.dispatchCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

@ExperimentalSerializationApi
class RewardProcessor {

    private val rewards = ConcurrentHashMap<RewardType, Reward>()

    /**
     * Load all available rewards from a configuration section.
     *
     * @param configuration The configuration section
     */
    fun loadAll(configuration: ConfigurationSection) {
        RewardType.values().forEach { type ->
            configuration.getConfigurationSection(type.name)?.let {
                load(it, type)
            }
        }
    }

    /**
     * Load a reward from a configuration section.
     *
     * @param configuration The configuration section
     * @param type The type of the reward
     */
    fun load(configuration: ConfigurationSection, type: RewardType) {
        rewards[type] = Reward(
            type,
            configuration.getStringList("messages"),
            configuration.getStringList("player-commands"),
            configuration.getStringList("console-commands")
        )
    }

    /**
     * Process a reward to the given player.
     *
     * @param type The type of the reward
     * @param player The player the reward is addressed to
     * @param additionalPlaceholders Additional placeholders
     */
    fun process(type: RewardType, player: Player, vararg additionalPlaceholders: Replacement) {
        rewards[type]?.let { reward ->
            reward.playerCommands.forEach {
                player.dispatchCommand(replacePlaceholders(it, player, *additionalPlaceholders))
            }

            reward.consoleCommands.forEach {
                Constants.CONSOLE.dispatchCommand(replacePlaceholders(it, player, *additionalPlaceholders))
            }

            reward.messages.forEach {
                player.sendMessage(MiniMessage.get().parse(replacePlaceholders(it, player, *additionalPlaceholders)))
            }
        }
    }

    private fun replacePlaceholders(
        string: String,
        player: Player,
        vararg additionalPlaceholders: Replacement
    ): String = Replacement.replace(
        string,
        Replacement("name", player.name),
        Replacement("uuid", player.uniqueId.toString()),
        *additionalPlaceholders
    )

}