package de.skyslycer.hmclink.plugin.commands

import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSerializationApi
class CommandRegister(
    distributor: MessageDistributor
) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val linkCommand = LinkCommand(scope, distributor)
    private val unlinkCommand = UnlinkCommand(scope, distributor)

    private lateinit var parsedLinkCommand: CommandAPICommand
    private lateinit var parsedUnlinkCommand: CommandAPICommand

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
            .executesPlayer(PlayerCommandExecutor { player, _ -> linkCommand.linkCommand(player) })
    }

    private fun unlinkCommand() {
        parsedUnlinkCommand = CommandAPICommand("unlink")
            .executesPlayer(PlayerCommandExecutor { player, _ -> unlinkCommand.unlinkCommand(player) })
    }

    private fun discordCommand() {
        CommandAPICommand("discord")
            .executesPlayer(PlayerCommandExecutor { player, _ -> linkCommand.linkCommand(player) })
            .withSubcommand(parsedLinkCommand)
            .withSubcommand(parsedUnlinkCommand)
            .register()
    }

}