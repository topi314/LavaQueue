package com.github.topi314.lavaqueue.protocol

import dev.arbjerg.lavalink.protocol.v4.Omissible
import dev.arbjerg.lavalink.protocol.v4.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmInline

@Serializable
data class Queue(
    val type: Type,
    val tracks: List<Track>,
    val userData: JsonObject,
) {
    @Serializable
    enum class Type {
        @SerialName("normal")
        NORMAL,

        @SerialName("repeat_track")
        REPEAT_TRACK,

        @SerialName("repeat_queue")
        REPEAT_QUEUE,
    }
}

@Serializable
data class QueueTrack(
    val encoded: String,
    val userData: Omissible<JsonObject> = Omissible.Omitted(),
)

@Serializable
data class QueueUpdate(
    val type: Omissible<Queue.Type> = Omissible.Omitted(),
    val tracks: Omissible<QueueTracks> = Omissible.Omitted(),
    val userData: Omissible<JsonObject> = Omissible.Omitted(),
)

@Serializable
@JvmInline
value class QueueTracks(val tracks: List<QueueTrack>)