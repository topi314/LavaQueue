package com.github.topi314.lavaqueue

import com.github.topi314.lavaqueue.protocol.QueueTrack
import com.github.topi314.lavaqueue.protocol.QueueTracks
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.lavalink.api.AudioPluginInfoModifier
import dev.arbjerg.lavalink.api.ISocketServer
import dev.arbjerg.lavalink.protocol.v4.ifPresent
import lavalink.server.util.decodeTrack
import lavalink.server.util.toTrack
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import com.github.topi314.lavaqueue.protocol.Queue as PQueue

fun Queue.toQueue(
    audioPlayerManager: AudioPlayerManager,
    pluginInfoModifiers: List<AudioPluginInfoModifier>
): PQueue {
    return PQueue(
        type,
        tracks.get().map { it.toTrack(audioPlayerManager, pluginInfoModifiers) },
        userData
    )
}

fun QueueTrack.toAudioTrack(audioPlayerManager: AudioPlayerManager): AudioTrack {
    val track = decodeTrack(audioPlayerManager, this.encoded)
    this.userData.ifPresent {
        track.userData = it
    }
    return track
}

fun QueueTracks.toAudioTracks(audioPlayerManager: AudioPlayerManager): List<AudioTrack> {
    return this.tracks.map { it.toAudioTrack(audioPlayerManager) }
}

fun socketContext(socketServer: ISocketServer, sessionId: String) =
    socketServer.sessions[sessionId] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")
