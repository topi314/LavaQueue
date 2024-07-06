package com.github.topi314.lavaqueue

import com.github.topi314.lavaqueue.protocol.EncodedTrack
import com.github.topi314.lavaqueue.protocol.EncodedTracks
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.lavalink.api.AudioPluginInfoModifier
import lavalink.server.util.decodeTrack
import lavalink.server.util.toTrack
import com.github.topi314.lavaqueue.protocol.Queue as PQueue

fun Queue.toQueue(
    audioPlayerManager: AudioPlayerManager,
    pluginInfoModifiers: List<AudioPluginInfoModifier>
): PQueue {
    return PQueue(
        type,
        tracks.get().map { it.toTrack(audioPlayerManager, pluginInfoModifiers) },
    )
}

fun EncodedTrack.toAudioTrack(audioPlayerManager: AudioPlayerManager): AudioTrack {
    return decodeTrack(audioPlayerManager, this.track)
}

fun EncodedTracks.toAudioTracks(audioPlayerManager: AudioPlayerManager): List<AudioTrack> {
    return this.tracks.map { it.toAudioTrack(audioPlayerManager) }
}