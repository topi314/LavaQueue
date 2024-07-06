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
        queue.queue.set(body.tracks.toAudioTracks(playerManager))
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
                queue.queue.addAll(index, it.toAudioTracks(playerManager))
                return
            }
            queue.queue.addAll(it.toAudioTracks(playerManager))
        }
    }

    @PutMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks")
    fun putQueueTracks(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @RequestBody tracks: EncodedTracks
    ) {
        lavaQueuePlugin.getQueue(sessionId, playerId).queue.set(tracks.toAudioTracks(playerManager).toMutableList())
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{playerId}/tracks/queue")
    fun deleteQueue(@PathVariable sessionId: String, @PathVariable playerId: Long) {
        lavaQueuePlugin.getQueue(sessionId, playerId).queue.clear()
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun getQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int
    ): Track? {
        return lavaQueuePlugin.getQueue(sessionId, playerId).queue.get(index)?.toTrack(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun postQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestBody track: EncodedTrack
    ) {
        lavaQueuePlugin.getQueue(sessionId, playerId).queue.set(index, track.toAudioTrack(playerManager))
    }

    @PatchMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    @PutMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/tracks/{index}")
    fun putQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestBody track: EncodedTrack
    ) {
        lavaQueuePlugin.getQueue(sessionId, playerId).queue.set(index, track.toAudioTrack(playerManager))
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/{index}")
    fun deleteQueueIndex(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestParam amount: Int?
    ) {
        if (amount == null) {
            lavaQueuePlugin.getQueue(sessionId, playerId).queue.removeAt(index)
            return
        }
        lavaQueuePlugin.getQueue(sessionId, playerId).queue.removeAt(index, amount)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{playerId}/queue/{index}/move")
    fun getQueueIndexMove(
        @PathVariable sessionId: String,
        @PathVariable playerId: Long,
        @PathVariable index: Int,
        @RequestParam position: Int
    ) {
        val queue = lavaQueuePlugin.getQueue(sessionId, playerId)
        val track = queue.queue.get(index) ?: return
        queue.queue.add(position, track)
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
