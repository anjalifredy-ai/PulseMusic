package com.pulsemusic.app.data

import com.pulsemusic.app.innertube.InnertubeClient
import com.pulsemusic.app.innertube.InnertubeSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PulseMusic own data layer (not a fork).
 * - Lyrics: real LRCLIB
 * - Catalog/search: Innertube (own client)
 * - Stream: player endpoint when URL is not ciphered; signature decipher = next hard step
 */
class MusicRepository {

    private val innertube = InnertubeClient()

    suspend fun getHomeQuickPicks(): List<Song> = withContext(Dispatchers.IO) {
        val remote = innertube.getHomeSuggestions()
        if (remote.isNotEmpty()) remote.map { it.toSong() }
        else DummyData.quickPicks
    }

    suspend fun getCoversAndRemixes(): List<Song> = withContext(Dispatchers.IO) {
        val remote = innertube.search("slowed remix")
        if (remote.isNotEmpty()) remote.map { it.toSong("Energize") }
        else DummyData.coversAndRemixes
    }

    suspend fun getRecentlyAdded(): List<Song> = withContext(Dispatchers.IO) {
        val remote = innertube.search("new music")
        if (remote.isNotEmpty()) remote.map { it.toSong() }
        else DummyData.recentlyAdded
    }

    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext DummyData.allSongs
        val remote = innertube.search(query)
        if (remote.isNotEmpty()) remote.map { it.toSong() }
        else DummyData.allSongs.filter {
            it.title.contains(query, true) || it.artist.contains(query, true)
        }
    }

    suspend fun resolveStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        innertube.getPlayer(videoId)?.streamUrl
    }

    suspend fun getPlayerDetails(videoId: String) = withContext(Dispatchers.IO) {
        innertube.getPlayer(videoId)
    }

    suspend fun getLyrics(song: Song): List<LyricLine> = withContext(Dispatchers.IO) {
        val durationSec = parseDurationSec(song.duration)
        val real = LyricsApi.fetch(song.title, song.artist, durationSec)
        if (real.isNotEmpty()) real else DummyData.demoLyrics(song.title)
    }

    private fun InnertubeSong.toSong(category: String = "All") = Song(
        id = videoId,
        title = title,
        artist = artist,
        coverUrl = thumbnailUrl,
        category = category,
        videoId = videoId
    )

    private fun parseDurationSec(duration: String): Int? {
        return try {
            val parts = duration.split(":")
            if (parts.size == 2) parts[0].toInt() * 60 + parts[1].toInt() else null
        } catch (_: Exception) {
            null
        }
    }
}
