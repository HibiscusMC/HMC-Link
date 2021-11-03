package de.skyslycer.hmclink.plugin.commands

import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.annotations.Permission
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
class CommandRegister(
    distributor: MessageDistributor
) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val linkCommand = LinkCommand(scope, distributor)
    private val unlinkCommand = UnlinkCommand(scope, distributor)

    private lateinit var parsedLinkCommand: CommandAPICommand
    private lateinit var parsedUnlinkCommand: CommandAPICommand
    private lateinit var parsedUnlinkTargetCommand: CommandAPICommand

    init {
        setup()
    }

    private fun setup() {
        linkCommand()
        unlinkCommand()
        discordCommand()
    }

    private fun linkCommand() {
        parsedLinkCommand = CommandAPICommand("link")
            .withArguments(OfflinePlayerArgument("target"))
            .executesPlayer(PlayerCommandExecutor { player, _ -> linkCommand.linkCommand(player) })

        parsedLinkCommand.register()
    }

    private fun unlinkCommand() {
        parsedUnlinkCommand = CommandAPICommand("unlink")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                unlinkCommand.unlinkCommand(
                    player,
                    Optional.empty()
                )
            })

        parsedUnlinkTargetCommand = CommandAPICommand("unlink")
            .withArguments(
                StringArgument("target")
                    .withPermission(CommandPermission.fromString("hmclink.unlinkothers"))
                    .replaceSuggestions {
                        Bukkit.getOfflinePlayers().mapNotNull { it.name }.toTypedArray()
                    })
            .executesPlayer(PlayerCommandExecutor { player, arguments ->
                unlinkCommand.unlinkCommand(
                    player,
                    Optional.ofNullable(arguments.getOrNull(0) as? String)
                )
            })

        parsedUnlinkCommand.register()
        parsedUnlinkTargetCommand.register()
    }

    private fun discordCommand() {
        CommandAPICommand("discord")
            .executesPlayer(PlayerCommandExecutor { player, _ -> linkCommand.linkCommand(player) })
            .withSubcommand(parsedLinkCommand)
            .withSubcommand(parsedUnlinkCommand)
            .withSubcommand(parsedUnlinkTargetCommand)
            .register()
    }

}