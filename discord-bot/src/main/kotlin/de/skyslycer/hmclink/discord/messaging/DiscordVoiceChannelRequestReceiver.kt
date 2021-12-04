package de.skyslycer.hmclink.discord.messaging

import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.messages.discord.LinkRemoveMessage
import de.skyslycer.hmclink.common.messages.updates.DiscordVoiceChannelRequestMessage
import de.skyslycer.hmclink.common.messages.updates.DiscordVoiceChannelUpdateMessage
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import de.skyslycer.hmclink.common.redis.receiving.MessageReceiver
import de.skyslycer.hmclink.discord.EnvironmentVariables
import de.skyslycer.hmclink.discord.utils.AliveUtils
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging

@ExperimentalSerializationApi
class DiscordVoiceChannelRequestReceiver(
    private val distributor: MessageDistributor,
    private val kord: Kord
) : MessageReceiver {

    private val logger = KotlinLogging.logger { }

    init {
        setup()
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Start the receiver.
     */
    override fun setup() {
        distributor.add<DiscordVoiceChannelRequestMessage>({ scope.launch { handleMessage(it as DiscordVoiceChannelRequestMessage) } })
    }

    private suspend fun handleMessage(message: DiscordVoiceChannelRequestMessage) {
        logger.info("Received voice channel request message. Sending voice channel data. (users: ${message.users})")

        val guild = kord.getGuild(Snowflake(EnvironmentVariables.GUILD_ID)) ?: return
        val users = HashMap<Long, String>()

        message.users.forEach {
            val member = guild.getMemberOrNull(Snowflake(it)) ?: return@forEach

            users[it] = member.getVoiceStateOrNull()?.getChannelOrNull()?.fetchChannelOrNull()?.name ?: return@forEach
        }

        if (users.isNotEmpty()) {
            AliveUtils.sendOrSave(
                DiscordVoiceChannelUpdateMessage(
                    distributor.serviceType,
                    ServiceType.BACKEND,
                    users
                ),
                distributor
            )
        }
    }

}