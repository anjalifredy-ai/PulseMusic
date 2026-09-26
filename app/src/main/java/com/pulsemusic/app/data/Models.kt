package com.pulsemusic.app.data

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: String = "3:45",
    val plays: String? = null,
    val category: String = "All",
    val streamUrl: String? = null,
    val videoId: String? = null
)

data class Playlist(
    val id: String,
    val title: String,
    val subtitle: String,
    val coverUrl: String
)

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val subscribers: String
)

data class LyricLine(
    val timeMs: Long,
    val text: String
)

/**
 * Offline fallback only when Innertube fails.
 * Uses real YouTube thumbnail URLs (i.ytimg.com) — not screenshot copies / picsum.
 */
object DummyData {

    private fun ytThumb(videoId: String) = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

    val quickPicks = listOf(
        Song("4NRXx6U8ABQ", "Blinding Lights", "The Weeknd", ytThumb("4NRXx6U8ABQ"), plays = "YouTube Music", category = "Energize", videoId = "4NRXx6U8ABQ"),
        Song("JGwWNGJdvx8", "Shape of You", "Ed Sheeran", ytThumb("JGwWNGJdvx8"), plays = "YouTube Music", category = "Romance", videoId = "JGwWNGJdvx8"),
        Song("kJQP7kiw5Fk", "Despacito", "Luis Fonsi", ytThumb("kJQP7kiw5Fk"), plays = "YouTube Music", category = "Energize", videoId = "kJQP7kiw5Fk"),
        Song("RgKAFK5djSk", "See You Again", "Wiz Khalifa", ytThumb("RgKAFK5djSk"), plays = "YouTube Music", category = "Sad", videoId = "RgKAFK5djSk"),
        Song("OPf0YbXqDm0", "Uptown Funk", "Mark Ronson", ytThumb("OPf0YbXqDm0"), plays = "YouTube Music", category = "Energize", videoId = "OPf0YbXqDm0"),
        Song("fJ9rUzIMcZQ", "Bohemian Rhapsody", "Queen", ytThumb("fJ9rUzIMcZQ"), plays = "YouTube Music", category = "Relax", videoId = "fJ9rUzIMcZQ")
    )

    val coversAndRemixes = listOf(
        Song("hT_nvWreIhg", "Counting Stars", "OneRepublic", ytThumb("hT_nvWreIhg"), category = "Energize", videoId = "hT_nvWreIhg"),
        Song("YQHsXMglC9A", "Hello", "Adele", ytThumb("YQHsXMglC9A"), category = "Sad", videoId = "YQHsXMglC9A"),
        Song("09R8_2nJtjg", "Sugar", "Maroon 5", ytThumb("09R8_2nJtjg"), category = "Romance", videoId = "09R8_2nJtjg"),
        Song("CevxZvSJLk8", "Roar", "Katy Perry", ytThumb("CevxZvSJLk8"), category = "Energize", videoId = "CevxZvSJLk8")
    )

    val recentlyAdded = quickPicks + coversAndRemixes

    val allSongs: List<Song>
        get() = (quickPicks + coversAndRemixes).distinctBy { it.id }

    val moods = listOf("All", "Relax", "Sleep", "Energize", "Sad", "Romance")

    val categories = listOf(
        "Trending", "Chill", "Workout", "Focus", "Party", "New releases", "Indie", "Hip-Hop"
    )

    fun demoLyrics(songTitle: String): List<LyricLine> = listOf(
        LyricLine(0, songTitle),
        LyricLine(4000, "Lyrics from LRCLIB when available"),
        LyricLine(8000, "Sign in for personalized home"),
        LyricLine(12000, "PulseMusic")
    )
}
