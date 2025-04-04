# LavaQueue

> [!IMPORTANT]
> This plugin *requires* Lavalink `v4.0.6` or greater

A simple queue plugin for [Lavalink](https://github.com/lavalink-devs/Lavalink) with a REST API.

## Summary

* [Installation](#installation)
* [API](#api)
    * [Common Types](#common-types)
    * [Endpoints](#endpoints)
      * [Queue](#endpoints)
        * [Get Queue](#get-queue)
        * [Modify Queue](#modify-queue)
      * [Queue Tracks](#queue-tracks)
        * [Next Queue Track](#next-queue-track)
        * [Previous Queue Track](#previous-queue-track)
        * [Add Queue Track](#add-queue-tracks)
        * [Update Queue Track](#update-queue-tracks)
        * [Get Queue Track](#get-queue-track)
        * [Set Queue Track](#set-queue-track)
        * [Delete Queue Tracks](#delete-queue-tracks)
        * [Move Queue Track](#move-queue-track)
      * [Queue History](#queue-history)
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

## API

The plugin provides a REST API to add, remove, and update tracks in the queue.
Fields marked with `?` are optional and types marked with `?` are nullable.

### Common Types

#### Queue Modes

| Type            | Description                                        |
|-----------------|----------------------------------------------------|
| `normal`        | Tracks will be played in the order they are added. |
| `repeat_track`  | A single track will be repeatedly played.          |
| `repeat_queue`  | The queue will repeat once it has ended.           |

---

#### Queue Object

| Field                | Type                                                               | Description                             |
|----------------------|--------------------------------------------------------------------|-----------------------------------------|
| mode                 | [string](#queue-modes)                                             | The mode of the queue.                  |
| tracks               | array of [track](https://lavalink.dev/api/rest.html#track) objects | An array of track objects in the queue. |

<details>
<summary>Example Payload</summary>

```json
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

### Endpoints

#### Get Queue

```http
GET /sessions/{sessionId}/players/{guildId}/queue
```

Response:

200 OK:
- [Queue Object](#queue-object)

---

#### Modify Queue

> [!NOTE]
> All fields are optional and only the fields you provide will be updated.

Modifies the queue. Overrides the existing tracks if the tracks key is present. Request body is a [queue object](#queue-object).

<details>
<summary>Example Payload</summary>

```json
{
  "mode": "repeat_track",
  "tracks": [
    {
      "encoded": "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRd"
    }
  ]
}
```
</details>

```http
PATCH /sessions/{sessionId}/players/{playerId}/queue
```

Response:

200 OK:
-  The next [track](https://lavalink.dev/api/rest.html#track) in the queue.
  
204 No Content:
- The queue was successfully updated, but there isn't a next track to return.

---

### Queue Tracks

#### Next Queue Track

Play the next track in the queue.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/next
```

Query Params:

| Field  | Type    | Description                                        |
|--------|---------|----------------------------------------------------|
| count? | integer | How many tracks to skip ahead to. Defaults to one. |

Response:

200 OK:
- The [track](https://lavalink.dev/api/rest.html#track) that was skipped to.

---

#### Previous Queue Track

Play the previously playing track.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/previous
```

Query Params:

| Field  | Type    | Description                                       |
|--------|---------|---------------------------------------------------|
| count? | integer | How many tracks to skip back to. Defaults to one. |

Response:

200 OK:
- The [track](https://lavalink.dev/api/rest.html#track) that was skipped to.

---

#### Add Queue Tracks

Adds tracks to the queue. Request body is an [update queue](#common-types) payload.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/tracks
```

Response:

200 OK:
- The next [track](https://lavalink.dev/api/rest.html#track) in the queue.

204 No Content:
- The queue was successfully updated, but the player is either playing a track, or there are no tracks in the queue.

---

#### Update Queue Tracks

Overrides the existing tracks in the queue. Request body is an array [update player tracks](https://lavalink.dev/api/rest#update-player-track).

```http
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks
```

Response:

200 OK:
- The next [track](https://lavalink.dev/api/rest.html#track) in the queue.

204 No Content:
- The queue was successfully updated, but there are no tracks in the queue.

---

#### Delete Queue

Clear all the tracks in the queue.

```http
DELETE /sessions/{sessionId}/players/{guildId}/queue
```

Response:

200 OK:
- The queue was successfully cleared.

---

#### Get Queue Track

Gets a track from the queue at the specified index.

```http
GET /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

Response:

200 OK:
- The [track](https://lavalink.dev/api/rest.html#track) at the specifed index.

---

#### Set Queue Track

Adds a track at the specified index. Request body is an [update player track](https://lavalink.dev/api/rest#update-player-track).

```http
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

Response:

200 OK:
- The track was successfully added at the specified index.

---

#### Delete Queue Tracks

Remove a track from the queue. If amount is provided, the specified number of elements after the index will be removed.

```http
DELETE /sessions/{sessionId}/players/{guildId}/queue/{index}
```

Query Params:

| Field   | Type    | Description                               |
|---------|---------|-------------------------------------------|
| amount? | integer | How many tracks to remove after the index.|

Response:

200 OK:
- The tracks were successfully removed.

---

#### Move Queue Track

Move a track to a different position. This does **not** remove the track at the original index.

```http
POST /sessions/{sessionId}/players/{guildId}/queue/{index}/move
```

Query Params:

| Field    | Type    | Description                |
|----------|---------|----------------------------|
| position | integer | The new index of the track.|

Response:

200 OK:
- The track was successfully moved.

---

### Queue History

#### Get Queue History

Gets the history of this queue.

```http
GET /sessions/{sessionId}/players/{guildId}/history
```

Response:

200 OK:
  Array of [track](https://lavalink.dev/api/rest.html#track) objects

---

#### Get Queue History Track

Gets a track from the history at the specified index.

```http
GET /sessions/{sessionId}/players/{guildId}/history/{index}
```

Response:

200 OK:
- The [track](https://lavalink.dev/api/rest.html#track) at the specified index.

---

### Events

#### QueueEndEvent

Fires when a queue has ended.

##### Payload Structure

| Field                | Type                                                               | Description                                  |
|----------------------|--------------------------------------------------------------------|----------------------------------------------|
| type                 | String                                                             | The type of event.                           |
| guildId              | String                                                             | The ID of the guild the queue has ended for. |

<details>
<summary>Example Payload</summary>

```json
{
  "op": "event",
  "type": "QueueEndEvent",
  "guildId": "...",
}
```
</details>