# LavaQueue

> [!IMPORTANT]
> This plugin *requires* Lavalink `v4.0.5` or greater

A simple queue plugin for [Lavalink](https://github.com/lavalink-devs/Lavalink) with a REST API.

## Summary

* [Installation](#installation)
* [API](#api)
  * [Get Queue](#get-queue)
  * [Modify Queue](#modify-queue)
  * [Next Queue Track](#next-queue-track)
  * [Previous Queue Track](#previous-queue-track)
  * [Add Queue Track](#add-queue-tracks)
  * [Update Queue Track](#update-queue-tracks)
  * [Get Queue Track](#get-queue-track)
  * [Add Queue Track](#add-queue-track)
  * [Delete Queue Track(s)](#delete-queue-track(s))
  * [Move Queue Track](#move-queue-track)
  * [Get Queue History](#get-queue-history)
  * [Get Queue History Track](#get-queue-history-track)
* [Events](#events)
  * [QueueEndEvent](#queueendevent)

## Installation

To install this plugin either download the latest release and place it into your `plugins` folder or add the following into your `application.yml`

Replace x.y.z with the latest version number or short commit hash.

```yaml
lavalink:
  plugins:
    - dependency: "com.github.topi314.lavaqueue:lavaqueue-plugin:x.y.z"
      snapshot: false # set to true if you want to use snapshot builds (see below)
```

Snapshot builds are available at https://maven.lavalink.dev/#/snapshots with the short commit hash as the version.

---

## API

The plugin provides a REST API to add, remove, and update tracks in the queue.

### Queue Modes

| Type            | Description                                        |
|-----------------|----------------------------------------------------|
| `normal`        | Tracks will be played in the order they are added. |
| `repeat_track`  | A single track will be repeatedly played.          |
| `repeat_queue`  | The queue will repeat once it has ended.           |

---

### Queue Object

| Field                | Type                                                               | Description                             |
|----------------------|--------------------------------------------------------------------|-----------------------------------------|
| [mode](#queue-modes) | string                                                             | The mode of the queue.                  |
| tracks               | array of [Track](https://lavalink.dev/api/rest.html#track) objects | An array of track objects in the queue. |

<details>
<summary>Example Payload</summary>

```json5
{
  "type": "normal",
  "tracks": [
    {
      "encoded": "...",
      "info": {},
      "pluginInfo": {},
      "userData": {}
    }
  ]
}
```

</details>

---

### Common Types

| Type                                              | Description                                                                                   |
|---------------------------------------------------|-----------------------------------------------------------------------------------------------|
| [track](https://lavalink.dev/api/rest.html#track) | A track object returned in API responses.                                                     |
| update_queue                                    | An array of [update player track](https://lavalink.dev/api/rest#update-player-track) objects.   |

<details>
<summary>Update Queue Payload</summary>

```json5
{
  [
    {
      "encoded": "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRd"
    }
  ]
}
```

</details>

---

### Get Queue

```http
GET /sessions/{sessionId}/players/{guildId}/queue
```

**Response:** [Queue Object](#queue-object)

---

### Modify Queue

> [!NOTE]
> All fields are optional and only the fields you provide will be updated.

Modifies the queue. Overrides the existing tracks if the tracks key is present. Request body is a [queue object](#queue-object).

```http
PATCH /sessions/{sessionId}/players/{playerId}/queue
```

**Response:** [Track Object](https://lavalink.dev/api/rest.html#track) or `204 NO CONTENT`

---

### Next Queue Track

Gets the next track in the queue. Plays the next track if the player isn't playing.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/next
```

**Response:** [Track Object](https://lavalink.dev/api/rest.html#track)

---

### Previous Queue Track

Gets the previously playing track. Plays the previous track if the player isn't playing.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/previous
```

**Response:** [Track Object](https://lavalink.dev/api/rest.html#track)

---

### Add Queue Tracks

Adds tracks to the queue. Request body is an [update queue](#common-types) payload.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/tracks
```

**Response:** [Next Track](https://lavalink.dev/api/rest.html#track)

---

### Update Queue Tracks

Overrides the existing tracks in the queue. Request body is an [update queue](#common-types) payload.

```http
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks
```

**Response:** [Next Track](https://lavalink.dev/api/rest.html#track)

---

### Delete Queue

```http
DELETE /sessions/{sessionId}/players/{guildId}/tracks/queue
```

**Response:** `204 NO CONTENT`

---

### Get Queue Track

Gets a track from the queue at the specified index.

```http
GET /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

**Response:** [Track Object](https://lavalink.dev/api/rest.html#track)

---

### Add Queue Track

Adds a track at the specified index. Reuqest body is an [update player track](https://lavalink.dev/api/rest#update-player-track).

```http
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

**Response:** `204 NO CONTENT`

---

### Delete Queue Track(s)

Remove a track from the queue. If amount is provided, the specified number of elements after the index will be removed.

```http
DELETE /sessions/{sessionId}/players/{guildId}/queue/{index}?amount=0
```

**Response:** `204 NO CONTENT`

---

### Move Queue Track

Move a track to a different position. This does *not* remove the track at the original index.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/{index}/move?position=0
```

**Response:** `204 NO CONTENT`

---

### Get Queue History

Gets the history of this queue.

```http
GET /sessions/{sessionId}/players/{guildId}/history
```

**Response:** Array of [track](https://lavalink.dev/api/rest.html#track) objects.

---

### Get Queue History Track

Gets a track from the history at the specified index.

```http
GET /sessions/{sessionId}/players/{guildId}/history/{index}
```

**Response:** [Track Object](https://lavalink.dev/api/rest.html#track)

---

## Events

### QueueEndEvent

Fires when a queue has ended.

<details>
<summary>Example Payload</summary>

```json5
{
  "op": "event",
  "type": "QueueEndEvent",
  "guildId": "...",
}
```
</details>