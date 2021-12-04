package de.skyslycer.hmclink.plugin.rewards

data class Reward(
    val type: RewardType,
    val messages: List<String>,
    val playerCommands: List<String>,
    val consoleCommands: List<String>
)