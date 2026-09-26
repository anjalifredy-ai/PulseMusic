# PulseMusic 🎵

Premium glassy YouTube Music–style Android client.

## Status

| Feature | Status |
|--------|--------|
| Glass UI (Home / Library / Search / Settings / Now Playing) | ✅ |
| Mood filters + Search | ✅ |
| Animations + posters | ✅ |
| **Media3 Player Service** (background-ready) | ✅ |
| **Lyrics UI** (synced highlight demo) | ✅ |
| MusicRepository architecture | ✅ |
| Real YouTube Music catalog (Innertube) | 🚧 Next |
| Real stream URLs + offline cache | 🚧 Next |
| Real lyrics (LRCLIB / YT) | 🚧 Next |

## Player service

`MusicService` uses **Media3 ExoPlayer + MediaSession** so once a stream URL is available (from Innertube / Piped / etc.), background playback and notification controls work.

## Lyrics

Now Playing → lyrics icon toggles fullscreen lyrics panel with active-line highlight.

## Real data next step

Plug an Innertube client into `MusicRepository` (see SimpMusic / InnerTune).  
`resolveStreamUrl(videoId)` and `search()` are the main hooks.

## Build

GitHub Actions builds debug APK on every push.  
Or open in Android Studio and Run.

Repo: https://github.com/anjalifredy-ai/PulseMusic
