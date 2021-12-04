package de.skyslycer.hmclink.discord.listeners

import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.on

interface Listener<T : Event> {

    suspend fun supply(event: T)

}

typealias ListenerHook = (Kord) -> Unit

suspend inline fun <reified T : Event> Listener<T>.hook(): ListenerHook = { kord ->
    kord.on<T> { this@hook.supply(this) }
}

suspend inline fun <reified T : Event> Listener<T>.add(listeners: MutableList<Pair<Listener<*>, ListenerHook>>) {
    listeners.add(this to this.hook())
}