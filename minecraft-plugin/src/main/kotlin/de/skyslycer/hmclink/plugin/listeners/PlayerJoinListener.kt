package de.skyslycer.hmclink.plugin.listeners

import de.skyslycer.hmclink.common.messages.link.LinkAnswerMessage
import de.skyslycer.hmclink.common.messages.link.LinkErrorMessage
import de.skyslycer.hmclink.common.messages.link.LinkSuccessMessage
import de.skyslycer.hmclink.common.messages.unlink.UnlinkAnswerMessage
import de.skyslycer.hmclink.plugin.chat.MessageHelper
import de.skyslycer.hmclink.plugin.queue.MessageQueue
import kotlinx.serialization.ExperimentalSerializationApi
import net.axay.kspigot.event.listen
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
class PlayerJoinListener(
    private val messageQueue: MessageQueue
) {

    init {
        setup()
    }

    private fun setup() {
        listen<PlayerJoinEvent> { event ->
            messageQueue[event.player.uniqueId]?.let { messages ->
                messages.forEach {
                    when (it) {
                        is LinkAnswerMessage -> MessageHelper.buildLinkReceiveMessage(it)
                        is LinkSuccessMessage -> MessageHelper.buildLinkSuccessMessage(it)
                        is LinkErrorMessage -> MessageHelper.buildLinkErrorMessage()
                        is UnlinkAnswerMessage ->
                            if (it.executor == event.player.uniqueId)
                                MessageHelper.buildUnlinkExecutorReceiveMessage(it)
                            else
                                MessageHelper.buildUnlinkReceiveMessage(it)
                    }
                }
            }

            messageQueue.remove(event.player.uniqueId)
        }
    }

}