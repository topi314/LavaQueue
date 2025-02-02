# LavaQueue

> [!IMPORTANT]
> This plugin *requires* Lavalink `v4.0.5` or greater

A simple queue plugin for [Lavalink](https://github.com/lavalink-devs/Lavalink) with a REST API.

## Summary

* [Installation](#installation)
* [API](#api)

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

The plugin provides a REST API to add, remove and update tracks in the queue.


### Queue Types

| Type         | Description                                            |
|--------------|--------------------------------------------------------|
| normal       | Tracks will be played in the first in first out order. |
| repeat_track | A singular track will be repeatedly played.            |
| repeat_queue | The queue will repeat once it has ended.               |

---

### Get Queue

```
GET /sessions/{sessionId}/players/{guildId}/queue
```

Response:

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

---

### Update Queue

Modifies the queue. Overrides the existing tracks if the tracks key is present.

```
PATCH /sessions/{sessionId}/players/{playerId}/queue
```

Request:

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

---

### Next Queue Track

Gets the next track in the queue. Plays the next track if the player isn't playing. Response is a [track](https://lavalink.dev/api/rest#track) object.

```
POST /sessions/{sessionId}/players/{guildId}/queue/next
```

---

### Previous Queue Track

Gets the previously playing track. Plays the previous track if the player isn't playing. Response is a [track](https://lavalink.dev/api/rest#track) object.

```
POST /sessions/{sessionId}/players/{guildId}/queue/previous
```

---

### Add Queue Tracks

Adds tracks to the queue. Response is the next queue [track](https://lavalink.dev/api/rest#track).

```
POST /sessions/{sessionId}/players/{guildId}/queue/tracks
```

Request:

```json5
{
  [
    {
      "encoded": "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRd"
    }
  ]
}
```

---

### Update Queue Tracks

Overrides the existing tracks in the queue. Response is the next queue [track](https://lavalink.dev/api/rest#track).

```
PUT /sessions/{sessionId}/players/{guildId}/queue/tracks
```

Request:

```json5
{
  [
    {
      "encoded": "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRd"
    }
  ]
}
```

---

### Delete Queue

```
DELETE /sessions/{sessionId}/players/{guildId}/tracks/queue
```

---

### Get Queue Track

Gets a track from the queue at the specified index. Response is a [track](https://lavalink.dev/api/rest#track) object.

```
GET /sessions/{sessionId}/players/{guildId}/queue/tracks/{index}
```

---

### Add Queue Track

Adds a track at the specified index. Reuqest body is an [update player track](https://lavalink.dev/api/rest#update-player-track).

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

Gets the history of this queue. Response is an array of [track](https://lavalink.dev/api/rest#track) objects.

```
GET /sessions/{sessionId}/players/{guildId}/history
```

---

### Get Queue History Track

Gets a track from the history at the specified index. Response is a [track](https://lavalink.dev/api/rest#track) object.

```
GET /sessions/{sessionId}/players/{guildId}/history/{index}
```

---

## Events

One new event has been added.

### QueueEndEvent

```json5
{
  "op": "event",
  "type": "QueueEndEvent",
  "guildId": "...",
}
```
