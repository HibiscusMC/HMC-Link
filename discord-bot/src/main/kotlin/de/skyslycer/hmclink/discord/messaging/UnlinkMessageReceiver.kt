package de.skyslycer.hmclink.discord.messaging

import de.skyslycer.hmclink.common.messages.discord.LinkRemoveMessage
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.discord.EnvironmentVariables
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging

@ExperimentalSerializationApi
class UnlinkMessageReceiver(
    private val distributor: MessageDistributor,
    private val kord: Kord
) : MessageReceiver {

    private val logger = KotlinLogging.logger {  }

    init {
        setup()
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<LinkRemoveMessage>({ scope.launch { handleMessage(it as LinkRemoveMessage) } })
    }

    private suspend fun handleMessage(message: LinkRemoveMessage) {
        logger.info("Received unlink message. Removing role and nickname... (id: ${message.id})")

        val guild = kord.getGuild(Snowflake(EnvironmentVariables.GUILD_ID)) ?: return
        val member = guild.getMemberOrNull(Snowflake(message.id)) ?: return

        member.removeRole(Snowflake(System.getenv(EnvironmentVariables.LINKED_ROLE)), "Unlink request")
        member.edit {
            nickname = null
        }
    }

}