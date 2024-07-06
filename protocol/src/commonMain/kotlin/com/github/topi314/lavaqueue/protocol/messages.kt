package com.github.topi314.lavaqueue.protocol

import dev.arbjerg.lavalink.protocol.v4.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("ready")
@Serializable
data class QueueEndEvent(
    val guildId: String
) {
    val op: Message.Op = Message.Op.Event
    val type: String = "QueueEndEvent"
}