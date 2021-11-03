package de.skyslycer.hmclink.plugin.queue

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

open class Queue<T> : ConcurrentHashMap<UUID, ArrayList<T>>() {

    /**
     * Add a value to the given uuid.
     *
     * @param uuid The uuid to add the value to
     * @param value The value to add
     */
    fun add(uuid: UUID, value: T) {
        if (this.containsKey(uuid)) {
            this[uuid]?.add(value)
        } else {
            this[uuid] = arrayListOf(value)
        }
    }

    /**
     * Remove a specific value from the given uuid.
     *
     * @param uuid The uuid to remove the value from
     * @param value The value to remove
     */
    fun remove(uuid: UUID, value: T) {
        this[uuid]?.remove(value)
    }

}