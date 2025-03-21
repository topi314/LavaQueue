package com.github.topi314.lavaqueue

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class TrackQueue(
    private var queue: MutableList<AudioTrack> = mutableListOf(),
) {

    fun add(track: AudioTrack) {
        queue.add(track)
    }

    fun add(index: Int, track: AudioTrack) {
        queue.add(index, track)
    }

    fun addAll(tracks: List<AudioTrack>) {
        queue.addAll(tracks)
    }

    fun addAll(index: Int, tracks: List<AudioTrack>) {
        queue.addAll(index, tracks)
    }

    fun set(tracks: List<AudioTrack>) {
        queue = tracks.toMutableList()
    }

    fun set(index: Int, track: AudioTrack) {
        queue[index] = track
    }

    fun get(): List<AudioTrack> {
        return queue.toList()
    }

    fun get(index: Int): AudioTrack? {
        return queue.getOrNull(index)
    }

    fun removeAt(index: Int): AudioTrack {
        return queue.removeAt(index)
    }

    fun removeAt(index: Int, amount: Int) {
        queue.subList(index, index + amount).clear()
    }

    fun clear() {
        queue.clear()
    }

    fun shuffle() {
        queue.shuffle()
    }

    fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return queue.isNotEmpty()
    }

    fun size(): Int {
        return queue.size
    }

    fun next(): AudioTrack? {
        return queue.firstOrNull()
    }

    fun removeNext(count: Int = 1): AudioTrack? {
        if (queue.isEmpty()) {
            return null
        }
        return if (count >= queue.size) {
            queue.removeFirstOrNull()
        } else {
            queue.subList(0, count).also { queue = queue.drop(count).toMutableList() }.last()
        }
    }

    fun last(): AudioTrack? {
        return queue.lastOrNull()
    }

    fun removeLast(count: Int = 1): AudioTrack? {
        if (queue.isEmpty()) {
            return null
        }
        return if (count >= queue.size) {
            queue.removeLastOrNull()
        } else {
            queue.subList(queue.size - count, queue.size).also { queue = queue.dropLast(count).toMutableList() }.first()
        }
    }

}