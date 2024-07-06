package com.github.topi314.lavaqueue.protocol

import dev.arbjerg.lavalink.protocol.v4.Omissible
import dev.arbjerg.lavalink.protocol.v4.Track
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Queue(
    val type: Type,
    val tracks: List<Track>,
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
data class QueueCreate(
    val type: Queue.Type,
    val tracks: EncodedTracks,
)

@Serializable
data class QueueUpdate(
    val type: Omissible<Queue.Type>,
    val tracks: Omissible<EncodedTracks>,
)

@Serializable
@JvmInline
value class EncodedTrack(val track: String)

@Serializable
@JvmInline
value class EncodedTracks(val tracks: List<EncodedTrack>)