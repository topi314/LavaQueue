package com.github.topi314.lavaqueue

import com.github.topi314.lavaqueue.protocol.Queue
import com.github.topi314.lavaqueue.protocol.QueueEndEvent
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.arbjerg.lavalink.api.IPlayer
import dev.arbjerg.lavalink.api.ISocketContext
import kotlinx.serialization.json.JsonObject

class Queue(
    private val context: ISocketContext,
    val player: IPlayer
) : AudioEventAdapter() {

    init {
        player.audioPlayer.addListener(this)
    }

    var mode = Queue.Mode.NORMAL
    val tracks = TrackQueue()
    val history = TrackQueue()
    var userData = JsonObject(emptyMap())

    fun next(count: Int = 1): AudioTrack? {
        val track = tracks.removeNext(count) ?: return null
        player.play(track)
        return track

    }

    fun previous(count: Int = 1): AudioTrack? {
        val track = history.removeLast(count) ?: return null
        player.play(track)
        return track
    }

    override fun onTrackEnd(unused: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        history.add(track.makeClone())
        if (endReason.mayStartNext) {
            val nextTrack = when (mode) {
                Queue.Mode.NORMAL -> next()
                Queue.Mode.REPEAT_TRACK -> {
                    val nextTrack = track.makeClone()
                    player.play(nextTrack)
                    nextTrack
                }

                Queue.Mode.REPEAT_QUEUE -> {
                    tracks.add(track.makeClone())
                    next()
                }
            }
            if (nextTrack == null) {
                context.sendMessage(QueueEndEvent.serializer(), QueueEndEvent(player.guildId.toString()))
            }
        }
    }

}