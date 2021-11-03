package de.skyslycer.hmclink.plugin.utils

import de.skyslycer.hmclink.plugin.Constants
import de.skyslycer.hmclink.plugin.chat.ChatMessageHandler
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.ExperimentalTime

@ExperimentalSerializationApi
class ErrorUtilities {

    companion object {
        /**
         * Send an error to the console.
         *
         * @param exception The specific exception
         */
        fun sendError(exception: SpecificException) {
            val logFile = log(exception)

            Constants.CONSOLE.sendMessage(
                ChatMessageHandler.getParsed(
                    String.format(
                        Constants.ERROR_MESSAGE,
                        exception.location,
                        exception.throwable.localizedMessage ?: "No description specified",
                        logFile
                    )
                )
            )

            if (exception.throwable.localizedMessage == null) exception.throwable.printStackTrace()
        }

        private fun log(exception: SpecificException): String {
            val directory = Constants.PLUGIN_PATH.resolve(Constants.LOG_DIRECTORY)

            if (!Files.exists(directory)) {
                Files.createDirectories(directory)
            }

            val logFile = getLogFile(directory)

            Files.createFile(logFile)

            Files.writeString(
                logFile,
                """
                    ==================
                       ERROR REPORT
                    ==================
                    Location: ${exception.location}
                    
                    == EXCEPTION==
                    ${exception.throwable.stackTraceToString()}
                """.trimIndent()
            )

            return logFile.fileName.toString()
        }

        private fun getLogFile(directory: Path, offset: Int = 0): Path {
            val newLog = directory.resolve("#${Files.list(directory).toList().size + offset}.log")

            return if (Files.exists(newLog)) {
                getLogFile(directory, offset + 1)
            } else {
                newLog
            }
        }
    }

    class SpecificException(val throwable: Throwable, val location: String)

}