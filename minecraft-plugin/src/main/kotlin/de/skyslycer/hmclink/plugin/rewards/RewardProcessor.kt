package de.skyslycer.hmclink.plugin.rewards

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

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
            configuration.getStringList("messages").mapNotNull { MiniMessage.get().parse(it) },
            configuration.getStringList("player-commands"),
            configuration.getStringList("console-commands")
        )
    }

}