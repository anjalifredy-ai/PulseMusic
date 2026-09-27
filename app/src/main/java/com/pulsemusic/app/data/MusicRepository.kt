package com.pulsemusic.app.data

import com.pulsemusic.app.innertube.InnertubeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository {

    private val innertube = InnertubeClient()

    suspend fun getHomeQuickPicks(): List<Song> = withContext(Dispatchers.IO) {
        // 1) Piped trending (reliable banners)
        val piped = StreamResolver.trending()
        if (piped.isNotEmpty()) return@withContext piped
        // 2) Innertube
        val remote = innertube.getHomeSuggestions().map { it.toSong() }
        if (remote.isNotEmpty()) return@withContext remote
        DummyData.quickPicks
    }

    suspend fun getCoversAndRemixes(): List<Song> = withContext(Dispatchers.IO) {
        val piped = StreamResolver.searchPiped("remix slowed")
        if (piped.isNotEmpty()) return@withContext piped
        val remote = innertube.search("remix").map { it.toSong("Energize") }
        if (remote.isNotEmpty()) return@withContext remote
        DummyData.coversAndRemixes
    }

    suspend fun getRecentlyAdded(): List<Song> = getHomeQuickPicks()

    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        // Piped search first (works + real thumbs)
        val piped = StreamResolver.searchPiped(query)
        if (piped.isNotEmpty()) return@withContext piped
        val remote = innertube.search(query).map { it.toSong() }
        if (remote.isNotEmpty()) return@withContext remote
        DummyData.allSongs.filter {
            it.title.contains(query, true) || it.artist.contains(query, true)
        }
    }

    suspend fun resolveStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        // Piped audio URL (playable without signature decipher)
        StreamResolver.resolveAudioUrl(videoId)
            ?: innertube.getPlayer(videoId)?.streamUrl
    }

    suspend fun getLyrics(song: Song): List<LyricLine> = withContext(Dispatchers.IO) {
        val durationSec = parseDurationSec(song.duration)
        val real = LyricsApi.fetch(song.title, song.artist, durationSec)
        if (real.isNotEmpty()) real else DummyData.demoLyrics(song.title)
    }

    private fun com.pulsemusic.app.innertube.InnertubeSong.toSong(category: String = "All") = Song(
        id = videoId,
        title = title.ifBlank { "Track" },
        artist = artist.ifBlank { "YouTube Music" },
        coverUrl = thumbnailUrl.ifBlank { "https://i.ytimg.com/vi/$videoId/hqdefault.jpg" },
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
