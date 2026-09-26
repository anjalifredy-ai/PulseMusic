package com.pulsemusic.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data layer.
 *
 * REAL lyrics: LRCLIB (https://lrclib.net)
 * Catalog / streams: still demo until Innertube client is wired.
 *
 * To make catalog + playback 100% real like SimpMusic:
 * 1. Add Innertube module (see maxrave-dev/SimpMusic or z-huang/InnerTune)
 * 2. Implement search / browse / player endpoints
 * 3. Resolve stream URL in resolveStreamUrl()
 * 4. Feed URL to MusicService / PlayerController
 */
class MusicRepository {

    suspend fun getHomeQuickPicks(): List<Song> = withContext(Dispatchers.IO) {
        DummyData.quickPicks
    }

    suspend fun getCoversAndRemixes(): List<Song> = withContext(Dispatchers.IO) {
        DummyData.coversAndRemixes
    }

    suspend fun getRecentlyAdded(): List<Song> = withContext(Dispatchers.IO) {
        DummyData.recentlyAdded
    }

    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) DummyData.allSongs
        else DummyData.allSongs.filter {
            it.title.contains(query, true) || it.artist.contains(query, true)
        }
    }

    suspend fun resolveStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        // TODO: Innertube player endpoint + signature
        null
    }

    /** Real lyrics from LRCLIB */
    suspend fun getLyrics(song: Song): List<LyricLine> = withContext(Dispatchers.IO) {
        val durationSec = parseDurationSec(song.duration)
        val real = LyricsApi.fetch(song.title, song.artist, durationSec)
        if (real.isNotEmpty()) real
        else DummyData.demoLyrics(song.title)
    }

    private fun parseDurationSec(duration: String): Int? {
        return try {
            val parts = duration.split(":")
            if (parts.size == 2) parts[0].toInt() * 60 + parts[1].toInt()
            else null
        } catch (_: Exception) {
            null
        }
    }
}
