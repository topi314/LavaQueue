package com.github.topi314.lavaqueue.plugin

import com.github.topi314.lavaqueue.protocol.*
import com.github.topi314.lavaqueue.toAudioTrack
import com.github.topi314.lavaqueue.toAudioTracks
import com.github.topi314.lavaqueue.toQueue
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import dev.arbjerg.lavalink.api.AudioPluginInfoModifier
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.arbjerg.lavalink.protocol.v4.Tracks
import dev.arbjerg.lavalink.protocol.v4.ifPresent
import lavalink.server.util.toTrack
import org.springframework.web.bind.annotation.*

@RestController
class LavaQueueRestHandler(
    private val playerManager: AudioPlayerManager,
    private val pluginInfoModifiers: List<AudioPluginInfoModifier>,
    private val lavaQueuePlugin: LavaQueuePlugin
) {

    @GetMapping("/v4/sessions/{sessionId}/players/{playerId}/queue")
    fun getQueue(@PathVariable sessionId: String, @PathVariable playerId: Long): Queue {
        return lavaQueuePlugin.getQueue(sessionId, playerId).toQueue(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{playerId}/queue")
    fun postQueue(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @RequestBody body: QueueCreate
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        queue.type = queue.type
        queue.tracks.set(body.tracks.toAudioTracks(playerManager))
        queue.playIfNotPlaying()
    }

    @PatchMapping("/v4/sessions/{sessionId}/players/{playerId}/queue")
    fun patchQueue(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @RequestParam index: Int?,
        @RequestBody body: QueueUpdate,
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        body.type.ifPresent {
            queue.type = it
        }

        body.tracks.ifPresent {
            if (index != null) {
                queue.tracks.addAll(index, it.toAudioTracks(playerManager))
                queue.playIfNotPlaying()
                return
            }
            queue.tracks.addAll(it.toAudioTracks(playerManager))
            queue.playIfNotPlaying()
        }
    }

    @PutMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks")
    fun putQueueTracks(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @RequestBody body: EncodedTracks
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        queue.tracks.set(body.toAudioTracks(playerManager).toMutableList())
        queue.playIfNotPlaying()
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{playerId}/tracks/queue")
    fun deleteQueue(@PathVariable sessionId: String, @PathVariable playerId: Long) {
        lavaQueuePlugin.getQueue(sessionId, playerId).tracks.clear()
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun getQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int
    ): Track? {
        return lavaQueuePlugin.getQueue(sessionId, playerId).tracks.get(index)?.toTrack(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun postQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestBody body: EncodedTrack
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        queue.tracks.set(index, body.toAudioTrack(playerManager))
        queue.playIfNotPlaying()
    }

    @PatchMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    @PutMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun putQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestBody body: EncodedTrack
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        queue.tracks.set(index, body.toAudioTrack(playerManager))
        queue.playIfNotPlaying()
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/{index}")
    fun deleteQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestParam amount: Int?
    ) {
        if (amount == null) {
            lavaQueuePlugin.getQueue(sessionId, playerId).tracks.removeAt(index)
            return
        }
        lavaQueuePlugin.getQueue(sessionId, playerId).tracks.removeAt(index, amount)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/{index}/move")
    fun moveQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestParam position: Int
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        val track = queue.tracks.get(index) ?: return
        queue.tracks.add(position, track)
        queue.playIfNotPlaying()
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{playerId}/history")
    fun getHistory(@PathVariable sessionId: String, @PathVariable playerId: Long): Tracks {
        return Tracks(lavaQueuePlugin.getQueue(sessionId, playerId).history.get().map {
            it.toTrack(
                playerManager,
                pluginInfoModifiers
            )
        })
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{playerId}/history/{index}")
    fun getHistoryIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int
    ): Track? {
        return lavaQueuePlugin.getQueue(sessionId, playerId).history.get(index)
            ?.toTrack(playerManager, pluginInfoModifiers)
    }

}
