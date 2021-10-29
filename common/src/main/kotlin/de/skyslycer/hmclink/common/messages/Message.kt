package de.skyslycer.hmclink.common.messages

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

@ExperimentalSerializationApi
@Serializable
abstract class Message {

    abstract val from: String
    abstract val to: String

    /**
     * Encode the message to a ByteArray.
     *
     * @return The encoded CBOR data
     */
    fun toByteArray(): ByteArray {
        return Cbor.encodeToByteArray(this)
    }

    companion object {
        /**
         * Decode a message from an encoded ByteArray.
         *
         * @param data The encoded CBOR data
         * @return The decoded message
         */
        fun fromByteArray(data: ByteArray): Message {
            return Cbor.decodeFromByteArray(data)
        }
    }

}