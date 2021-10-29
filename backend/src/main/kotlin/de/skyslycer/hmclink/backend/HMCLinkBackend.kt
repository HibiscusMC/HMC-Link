package de.skyslycer.hmclink.backend

import de.skyslycer.hmclink.backend.constants.EnvironmentVariables
import de.skyslycer.hmclink.backend.database.DatabaseHandler
import de.skyslycer.hmclink.backend.messages.LinkGenerationReceiver
import de.skyslycer.hmclink.common.ServiceType
import de.skyslycer.hmclink.common.redis.MessageHandler
import de.skyslycer.hmclink.common.redis.receiving.MessageDistributor
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import kotlin.system.exitProcess

@ExperimentalSerializationApi
fun main() {
    HMCLinkBackend().start()
}

@ExperimentalSerializationApi
class HMCLinkBackend {

    private val logger = KotlinLogging.logger { }

    private val messageHandler = MessageHandler(
        ServiceType.BACKEND,
        System.getenv(EnvironmentVariables.REDIS_HOST),
        System.getenv(EnvironmentVariables.REDIS_PORT).toInt()
    )

    private val distributor = MessageDistributor(
        messageHandler,
        ServiceType.BACKEND
    )

    /**
     * A basic method to start the backend.
     */
    fun start() {
        logger.info("Starting the HMC Link Backend!")

        logger.info("Initializing Redis...")
        val redisReturn = messageHandler.createJedis()
        if (redisReturn.isPresent) {
            logger.error("Couldn't enable Redis! Shutting down the backend.", redisReturn.get())
            exitProcess(1)
        }
        logger.info("Successfully enabled Redis!")

        logger.info("Initializing database...")
        val databaseReturn = DatabaseHandler.connect()
        if (databaseReturn.isPresent) {
            logger.error("Couldn't enable the database! Shutting down the backend.", redisReturn.get())
            exitProcess(1)
        }
        logger.info("Successfully enabled the database!")

        logger.info("Setting up message listeners...")
        LinkGenerationReceiver(distributor, messageHandler)
    }

}