package com.github.topi314.lavaqueue.protocol

import dev.arbjerg.lavalink.protocol.v4.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("ready")
@Serializable
@Suppress("DataClassPrivateConstructor")
data class QueueEndEvent private constructor(
    val op: Message.Op,
    val type: String,
    val guildId: String
) {
    constructor(guildId: String) : this(
        Message.Op.Event,
        "QueueEndEvent",
        guildId,
    )
}