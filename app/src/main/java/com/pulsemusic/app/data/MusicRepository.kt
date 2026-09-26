package com.pulsemusic.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Data layer.
 *
 * CURRENT: returns curated demo catalog (posters work offline-friendly via picsum).
 * NEXT: plug Innertube / InnerTune-style client here to fetch real YouTube Music
 * home, search, charts, and stream URLs.
 *
 * Reference projects:
 * - maxrave-dev/SimpMusic
 * - z-huang/InnerTune
 */
class MusicRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

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

    /**
     * Placeholder for stream URL resolution.
     * Real implementation uses Innertube player endpoint + signature decipher.
     */
    suspend fun resolveStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        // TODO: Innertube player request
        null
    }

    /**
     * Lyrics: try public sources later (lrclib.net, etc.)
     */
    suspend fun getLyrics(song: Song): List<LyricLine> = withContext(Dispatchers.IO) {
        // Try LRCLIB-style public API later
        DummyData.demoLyrics(song.title)
    }

    fun ping(url: String): Boolean {
        return try {
            val req = Request.Builder().url(url).head().build()
            client.newCall(req).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
