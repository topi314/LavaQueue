# LavaQueue

> [!IMPORTANT]
> This plugin *requires* Lavalink `v4.0.5` or greater

A simple queue plugin for [Lavalink](https://github.com/lavalink-devs/Lavalink) with a REST API.

## Summary

* [Installation](#installation)
* [API](#api)
* [Get Queue](#get-queue)
* [Update Queue](#update-queue)
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

### Queue Types

| Type            | Description                                        |
|-----------------|----------------------------------------------------|
| `normal`        | Tracks will be played in the order they are added. |
| `repeat_track`  | A singular track will be repeatedly played.        |
| `repeat_queue`  | The queue will repeat once it has ended.           |

---

### Common Types

| Type                                                                          | Description                                               |
|-------------------------------------------------------------------------------|-----------------------------------------------------------|
| [track](https://lavalink.dev/api/rest.html#track)                             | A track object returned in API responses.                 |
| [update_player_track](https://lavalink.dev/api/rest.html#update-player-track) | An update player track that can be sent in API requests.  | 

---

### Queue Object

| Field  | Type   | Description                             |
|--------|--------|-----------------------------------------|
| type   | string | the type of queue.                      |
| tracks | array  | An array of track objects in the queue. |

<details>
<summary>Example Payload</summary>

```json5
{
  "type": "normal",
  "tracks": [
    {
      "encoded": "...",
      "info": "{}",
      "pluginInfo": "{}",
      "userData": "{}"
    }
  ]
}
```

</details>

---

## API

The plugin provides a REST API to add, remove, and update tracks in the queue.

### Get Queue

Returns a [queue object](#queue-object).

```
GET /sessions/{sessionId}/players/{guildId}/queue
```

---

### Update Queue

Modifies the queue. Overrides the existing tracks if the tracks key is present.

```
PATCH /sessions/{sessionId}/players/{playerId}/queue
```

<details>
<summary>Example Payload</summary>

```json5
{
  "type": "normal",
  "tracks": [
    {
      "encoded":"QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRd"
    }
  ]
}
```
</details>

---

### Next Queue Track

Gets the next track in the queue. Plays the next track if the player isn't playing. Response is a track object.

```
POST /sessions/{sessionId}/players/{guildId}/queue/next
```

---

### Previous Queue Track

Gets the previously playing track. Plays the previous track if the player isn't playing. Response is a track object.

```
POST /sessions/{sessionId}/players/{guildId}/queue/previous
```

---

### Add Queue Tracks

Adds tracks to the queue. Response is the next queue track.

```
POST /sessions/{sessionId}/players/{guildId}/queue/tracks
```

<details>
<summary>Example Payload</summary>

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

### Update Queue Tracks

Overrides the existing tracks in the queue. Response is the next queue track.

```
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks
```

<details>
<summary>Example Payload</summary>

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

### Delete Queue

```
DELETE /sessions/{sessionId}/players/{guildId}/tracks/queue
```

---

### Get Queue Track

Gets a track from the queue at the specified index. Response is a track object.

```
GET /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

---

### Add Queue Track

Adds a track at the specified index. Reuqest body is an update player track.

```
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

---

### Delete Queue Track(s)

Deletes a track from the queue. If amount is provided, the specified number of elements after the index will be removed.

```
DELETE /sessions/{sessionId}/players/{guildId}/queue/{index}?amount=0
```

---

### Move Queue Track

Move a track to a different position. This does *not* remove the track at the original index.

```
POST /sessions/{sessionId}/players/{guildId}/queue/{index}/move?position=0
```

---

### Get Queue History

Gets the history of this queue. Response is an array of track objects.

```
GET /sessions/{sessionId}/players/{guildId}/history
```

---

### Get Queue History Track

Gets a track from the history at the specified index. Response is a track object.

```
GET /sessions/{sessionId}/players/{guildId}/history/{index}
```

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