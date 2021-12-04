package de.skyslycer.hmclink.discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import java.nio.file.Paths

object Constants {

    val WAITING_MESSAGES_DIRECTORY: Path = Paths.get("waiting_messages")
    val WAITING_SCOPE = CoroutineScope(Dispatchers.Default)

}