# PulseMusic 🎵

**Own** premium YouTube Music–style Android client (not a SimpMusic fork).

## Built for publishing under your brand

| Layer | Status |
|-------|--------|
| Glass UI | ✅ |
| Media3 player service | ✅ |
| Real lyrics (LRCLIB) | ✅ |
| **Own Innertube client** (search / browse / player) | ✅ foundation |
| Real posters from YT Music search | ✅ when API responds |
| Stream play when URL not ciphered | ✅ partial |
| Full signature decipher (all streams) | 🚧 next |

## Architecture (yours)

```
app/
  innertube/InnertubeClient.kt   ← own YT Music requests
  data/MusicRepository.kt        ← catalog + lyrics + stream resolve
  data/LyricsApi.kt              ← LRCLIB
  player/MusicService.kt         ← Media3 background
  player/PlayerController.kt
  ui/                            ← glass screens
```

## Next for reliable audio

Many player responses use `signatureCipher`. Need JS-based decipher (like NewPipe Extractor) to unlock all formats. That is the main remaining playback hard step.

## Build

GitHub Actions → debug APK on every push.  
https://github.com/anjalifredy-ai/PulseMusic

---
PulseMusic — own engine, own UI, own release path.
