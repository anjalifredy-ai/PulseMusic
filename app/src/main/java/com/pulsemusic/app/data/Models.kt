package com.pulsemusic.app.data

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: String = "3:45",
    val plays: String? = null,
    val category: String = "All",
    /** Direct stream URL when available (from Innertube / Piped / etc.) */
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

object DummyData {
    val quickPicks = listOf(
        Song("1", "Safar", "Bayaan, Sherazam", "https://picsum.photos/seed/safar99/400/400", plays = "12M views", category = "Relax", videoId = "demo1"),
        Song("2", "LUZ ROJA (Slowed)", "bxkq", "https://picsum.photos/seed/luzroja99/400/400", plays = "36M views", category = "Energize", videoId = "demo2"),
        Song("3", "X-COOL! (Ultra Slowed)", "tienanh109, HDN, MC K3", "https://picsum.photos/seed/xcool99/400/400", plays = "8.2M", category = "Energize"),
        Song("4", "Barsaat", "Banjaare, Roni", "https://picsum.photos/seed/barsaat99/400/400", plays = "5.1M", category = "Romance"),
        Song("5", "Blinding Lights", "The Weeknd", "https://picsum.photos/seed/blinding/400/400", plays = "1.2B views", category = "Energize", videoId = "4NRXx6U8ABQ"),
        Song("6", "Night Changes", "One Direction", "https://picsum.photos/seed/nightch/400/400", plays = "890M views", category = "Romance"),
        Song("7", "Someone You Loved", "Lewis Capaldi", "https://picsum.photos/seed/someone/400/400", plays = "720M", category = "Sad"),
        Song("8", "Stay", "The Kid LAROI, Justin Bieber", "https://picsum.photos/seed/stay99/400/400", plays = "950M", category = "Energize")
    )

    val coversAndRemixes = listOf(
        Song("9", "X-COOL! (Super Slowed)", "tienanh109", "https://picsum.photos/seed/xcool2x/400/400", plays = "6M plays", category = "Energize"),
        Song("10", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luzroja2/400/400", plays = "20M plays", category = "Energize"),
        Song("11", "After Hours (Slowed)", "The Weeknd", "https://picsum.photos/seed/afterhours/400/400", plays = "15M", category = "Sad"),
        Song("12", "Softly", "Karan Aujla", "https://picsum.photos/seed/softly/400/400", plays = "45M", category = "Romance")
    )

    val recentlyAdded = listOf(
        Song("13", "LUZ ROJA (Slowed)", "bxkq", "https://picsum.photos/seed/luz3x/300/300", category = "Energize"),
        Song("14", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luz4x/300/300", plays = "20M plays", category = "Energize"),
        Song("15", "IZ ROJA (Vocal Remix)", "bxkq, Hilal Bilen", "https://picsum.photos/seed/izroja2/300/300", category = "Energize"),
        Song("16", "FUNK ABNORMAL", "DJ V12, SXID", "https://picsum.photos/seed/funk99/300/300", category = "Energize"),
        Song("17", "CALIENTE (Over Slowed)", "Unknown", "https://picsum.photos/seed/caliente99/300/300", category = "Energize"),
        Song("18", "Die For You", "The Weeknd", "https://picsum.photos/seed/dieforyou/300/300", plays = "480M", category = "Sad"),
        Song("19", "Peaches", "Justin Bieber", "https://picsum.photos/seed/peaches/300/300", plays = "610M", category = "Relax")
    )

    val allSongs: List<Song>
        get() = (quickPicks + coversAndRemixes + recentlyAdded).distinctBy { it.id }

    val moods = listOf("All", "Relax", "Sleep", "Energize", "Sad", "Romance")

    val categories = listOf(
        "Quick picks", "Covers & remixes", "Trending", "Chill", "Workout", "Focus", "Party", "New releases"
    )

    /** Demo synced lyrics for UI */
    fun demoLyrics(songTitle: String): List<LyricLine> = listOf(
        LyricLine(0, "♪ $songTitle"),
        LyricLine(3000, "Playing in PulseMusic"),
        LyricLine(6000, "Premium glassy player"),
        LyricLine(9000, "Lyrics will sync when"),
        LyricLine(12000, "real provider is connected"),
        LyricLine(15000, "(LRCLIB / YouTube / AI)"),
        LyricLine(18000, "Stay tuned..."),
        LyricLine(21000, "")
    )
}
