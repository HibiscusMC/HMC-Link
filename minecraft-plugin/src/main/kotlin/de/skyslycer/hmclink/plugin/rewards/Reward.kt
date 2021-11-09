package de.skyslycer.hmclink.plugin.rewards

import net.kyori.adventure.text.Component

data class Reward(
    val type: RewardType,
    val messages: List<String>,
    val playerCommands: List<String>,
    val consoleCommands: List<String>
)