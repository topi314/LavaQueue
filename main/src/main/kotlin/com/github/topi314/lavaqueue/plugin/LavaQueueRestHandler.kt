package com.github.topi314.lavaqueue.plugin

import com.github.topi314.lavaqueue.protocol.Queue
import com.github.topi314.lavaqueue.protocol.QueueTrack
import com.github.topi314.lavaqueue.protocol.QueueTracks
import com.github.topi314.lavaqueue.protocol.QueueUpdate
import com.github.topi314.lavaqueue.socketContext
import com.github.topi314.lavaqueue.toAudioTrack
import com.github.topi314.lavaqueue.toAudioTracks
import com.github.topi314.lavaqueue.toQueue
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.arbjerg.lavalink.api.AudioPluginInfoModifier
import dev.arbjerg.lavalink.api.ISocketServer
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.arbjerg.lavalink.protocol.v4.Tracks
import dev.arbjerg.lavalink.protocol.v4.ifPresent
import lavalink.server.util.toTrack
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class LavaQueueRestHandler(
    private val socketServer: ISocketServer,
    private val playerManager: AudioPlayerManager,
    private val pluginInfoModifiers: List<AudioPluginInfoModifier>,
    private val lavaQueuePlugin: LavaQueuePlugin
) {

    @GetMapping("/v4/sessions/{sessionId}/players/{guildId}/queue")
    fun getQueue(@PathVariable sessionId: String, @PathVariable guildId: Long): Queue {
        return lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).toQueue(playerManager, pluginInfoModifiers)
    }

    @PatchMapping("/v4/sessions/{sessionId}/players/{guildId}/queue")
    fun patchQueue(@PathVariable sessionId: String, @PathVariable guildId: Long, @RequestBody body: QueueUpdate): Track {
        val queue = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId)
        body.mode.ifPresent {
            queue.mode = it
        }

        var track: AudioTrack? = null
        body.tracks.ifPresent {
            queue.tracks.set(it.toAudioTracks(playerManager))
            track = queue.next()
        }

        body.userData.ifPresent {
            queue.userData = it
        }

        track ?: throw ResponseStatusException(HttpStatus.NO_CONTENT, "")
        return track!!.toTrack(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/next")
    fun postQueueNext(@PathVariable sessionId: String, @PathVariable guildId: Long): Track {
        val track = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).next() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No next track found")
        return track.toTrack(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/previous")
    fun postQueuePrevious(@PathVariable sessionId: String, @PathVariable guildId: Long): Track {
        val track = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).previous() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No previous track found")
        return track.toTrack(playerManager, pluginInfoModifiers)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/tracks")
    fun postQueueTracks(@PathVariable sessionId: String, @PathVariable guildId: Long, @RequestBody tracks: QueueTracks): Track {
        val queue = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId)
        queue.tracks.addAll(tracks.toAudioTracks(playerManager))
        val track = queue.next() ?: throw ResponseStatusException(HttpStatus.NO_CONTENT, "")
        return track.toTrack(playerManager, pluginInfoModifiers)
    }

    @PutMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/tracks")
    fun putQueueTracks(@PathVariable sessionId: String, @PathVariable guildId: Long, @RequestBody tracks: QueueTracks): Track {
        val queue = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId)
        queue.tracks.set(tracks.toAudioTracks(playerManager))
        val track = queue.next() ?: throw ResponseStatusException(HttpStatus.NO_CONTENT, "")
        return track.toTrack(playerManager, pluginInfoModifiers)
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{guildId}/queue")
    fun deleteQueue(@PathVariable sessionId: String, @PathVariable guildId: Long) {
        lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).tracks.clear()
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/tracks/{index}")
    fun getQueueIndex(@PathVariable sessionId: String, @PathVariable guildId: Long, @PathVariable index: Int): Track? {
        return lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).tracks.get(index)?.toTrack(playerManager, pluginInfoModifiers)
    }

    @PutMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/tracks/{index}")
    fun putQueueIndex(@PathVariable sessionId: String, @PathVariable guildId: Long, @PathVariable index: Int, @RequestBody track: QueueTrack) {
        val queue = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId)
        queue.tracks.set(index, track.toAudioTrack(playerManager))
    }

    @DeleteMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/{index}")
    fun deleteQueueIndex(@PathVariable sessionId: String, @PathVariable guildId: Long, @PathVariable index: Int, @RequestParam amount: Int?) {
        if (amount == null) {
            lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).tracks.removeAt(index)
            return
        }
        lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).tracks.removeAt(index, amount)
    }

    @PostMapping("/v4/sessions/{sessionId}/players/{guildId}/queue/{index}/move")
    fun getQueueIndexMove(@PathVariable sessionId: String, @PathVariable guildId: Long, @PathVariable index: Int, @RequestParam position: Int) {
        val queue = lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId)
        val track = queue.tracks.get(index) ?: return
        queue.tracks.add(position, track)
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{guildId}/history")
    fun getHistory(@PathVariable sessionId: String, @PathVariable guildId: Long): Tracks {
        return Tracks(lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).history.get().map {
            it.toTrack(
                playerManager,
                pluginInfoModifiers
            )
        })
    }

    @GetMapping("/v4/sessions/{sessionId}/players/{guildId}/history/{index}")
    fun getHistoryIndex(@PathVariable sessionId: String, @PathVariable guildId: Long, @PathVariable index: Int): Track? {
        return lavaQueuePlugin.getQueue(socketContext(socketServer, sessionId), guildId).history.get(index)
            ?.toTrack(playerManager, pluginInfoModifiers)
    }

}
