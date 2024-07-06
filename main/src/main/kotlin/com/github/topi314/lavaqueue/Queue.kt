package com.github.topi314.lavaqueue

import com.github.topi314.lavaqueue.protocol.Queue
import com.github.topi314.lavaqueue.protocol.QueueEndEvent
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.arbjerg.lavalink.api.IPlayer
import dev.arbjerg.lavalink.api.ISocketContext

class Queue(
    private val context: ISocketContext,
    private val player: IPlayer
) : AudioEventAdapter() {

    var type = Queue.Type.NORMAL
    val queue = TrackQueue()
    val history = TrackQueue()

    fun next(): AudioTrack? {
        if (queue.isNotEmpty()) {
            val track = queue.next()
            player.play(track!!)
            return track
        }
        return null
    }

    fun previous(): AudioTrack? {
        if (history.isNotEmpty()) {
            val track = history.removeLast()
            player.play(track!!)
            return track
        }
        return null
    }

    override fun onTrackEnd(unused: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        history.add(track.makeClone())
        if (endReason.mayStartNext) {
            val nextTrack = when (type) {
                Queue.Type.NORMAL -> next()
                Queue.Type.REPEAT_TRACK -> {
                    val nextTrack = track.makeClone()
                    player.play(nextTrack)
                    nextTrack
                }

                Queue.Type.REPEAT_QUEUE -> {
                    queue.add(track.makeClone())
                    next()
                }
            }
            if (nextTrack == null) {
                context.sendMessage(QueueEndEvent.serializer(), QueueEndEvent(player.guildId.toString()))
            }
        }
    }

}