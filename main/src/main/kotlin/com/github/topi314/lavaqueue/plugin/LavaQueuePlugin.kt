package com.github.topi314.lavaqueue.plugin

import com.github.topi314.lavaqueue.Queue
import dev.arbjerg.lavalink.api.IPlayer
import dev.arbjerg.lavalink.api.ISocketContext
import dev.arbjerg.lavalink.api.PluginEventHandler
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class LavaQueuePlugin : PluginEventHandler() {

    private val queues = mutableMapOf<String, MutableMap<Long, Queue>>()

    override fun onWebSocketOpen(context: ISocketContext, resumed: Boolean) {
        if (resumed) {
            return
        }
        queues[context.sessionId] = mutableMapOf()
    }

    override fun onSocketContextDestroyed(context: ISocketContext) {
        queues.remove(context.sessionId)
    }

    override fun onNewPlayer(context: ISocketContext, player: IPlayer) {
        if (queues[context.sessionId]?.get(player.guildId) != null) {
            return
        }
        queues[context.sessionId]?.put(player.guildId, Queue(context, player))
    }

    override fun onDestroyPlayer(context: ISocketContext, player: IPlayer) {
        queues[context.sessionId]?.remove(player.guildId)
    }

    fun getQueue(context: ISocketContext, guildId: Long): Queue {
        val queue = queues[context.sessionId] ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Session ${context.sessionId} not found")

        // this is a bit funny, since this calls onNewPlayer which creates the queue if it doesn't exist
        context.getPlayer(guildId);

        return queue[guildId] ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create queue for guild $guildId in session ${context.sessionId}")
    }

}