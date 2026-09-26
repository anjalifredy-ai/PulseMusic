# PulseMusic 🎵

Premium glassy YouTube Music–style Android client.

## What is REAL right now

| Feature | Status |
|--------|--------|
| Glass UI + animations | ✅ |
| Search / moods / categories | ✅ |
| Media3 background **player service** | ✅ |
| **Real lyrics** via [LRCLIB](https://lrclib.net) API | ✅ |
| Catalog posters (demo + placeholders) | ✅ |
| YouTube Music **catalog + stream URLs** (Innertube) | ❌ Not yet |

## Lyrics (real)

Now Playing → lyrics icon. App calls:

```
GET https://lrclib.net/api/get?track_name=...&artist_name=...
```

Synced LRC is parsed and highlighted while “playing”.

## Why catalog/stream is not fully real yet

YouTube Music has **no public streaming API**. Apps like SimpMusic / InnerTune / Metrolist reverse-engineer **Innertube** (private client API), handle tokens, signatures, and broken endpoints when Google changes them. That is months of work and ongoing maintenance — not something that can be finished in one chat.

**Architecture is ready:**
- `MusicRepository.resolveStreamUrl(videoId)`
- `MusicService` + `PlayerController` already play any HTTPS stream URL

Next real step = wire an Innertube client (or fork SimpMusic core) into the repository.

## Build

GitHub Actions builds APK on push.  
https://github.com/anjalifredy-ai/PulseMusic
