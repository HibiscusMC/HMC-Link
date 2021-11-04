package de.skyslycer.hmclink.backend.messages

import de.skyslycer.hmclink.backend.database.tables.DiscordMessageTable
import de.skyslycer.hmclink.backend.utils.AliveUtilities
import de.skyslycer.hmclink.common.redis.Channels
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@ExperimentalSerializationApi
class RepeatedAliveChecker {

    companion object {
        private val scope = CoroutineScope(Dispatchers.Default)

        fun setup(distributor: MessageDistributor) {
           scope.launch {
               start(distributor)
           }
        }

        private suspend fun start(distributor: MessageDistributor) {
            check(distributor)

            delay(10 * 60 * 1000)

            start(distributor)
        }

        private fun check(distributor: MessageDistributor) {
            AliveUtilities.onAliveInTime(3, distributor, scope, distributor, {
                newSuspendedTransaction {
                    DiscordMessageTable.selectAll().forEach {
                        distributor.messageHandler.jedis.publish(
                            Channels.STANDARD.toByteArray(),
                            it[DiscordMessageTable.message]
                        )
                    }
                }
            })
        }
    }

}