package com.pulsemusic.app.data

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: String = "3:45",
    val plays: String? = null,
    val category: String = "All"
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

object DummyData {
    // Using reliable placeholder + some known YT thumbnail style URLs
    // Real YT thumbnails: https://i.ytimg.com/vi/VIDEO_ID/hqdefault.jpg
    val quickPicks = listOf(
        Song("1", "Safar", "Bayaan, Sherazam", "https://i.ytimg.com/vi/8Gw8vZ8Y0Z0/hqdefault.jpg", plays = "12M views", category = "Relax"),
        Song("2", "LUZ ROJA (Slowed)", "bxkq", "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg", plays = "36M views", category = "Energize"),
        Song("3", "X-COOL! (Ultra Slowed)", "tienanh109, HDN, MC K3", "https://picsum.photos/seed/xcool99/400/400", plays = "8.2M", category = "Energize"),
        Song("4", "Barsaat", "Banjaare, Roni", "https://picsum.photos/seed/barsaat99/400/400", plays = "5.1M", category = "Romance"),
        Song("5", "Blinding Lights", "The Weeknd", "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg", plays = "1.2B views", category = "Energize"),
        Song("6", "Night Changes", "One Direction", "https://i.ytimg.com/vi/syFZfO_wfMQ/hqdefault.jpg", plays = "890M views", category = "Romance"),
        Song("7", "Someone You Loved", "Lewis Capaldi", "https://i.ytimg.com/vi/zABLecsR5UE/hqdefault.jpg", plays = "720M", category = "Sad"),
        Song("8", "Stay", "The Kid LAROI, Justin Bieber", "https://i.ytimg.com/vi/kTJczUoc97U/hqdefault.jpg", plays = "950M", category = "Energize")
    )

    val coversAndRemixes = listOf(
        Song("9", "X-COOL! (Super Slowed)", "tienanh109, HDN, MC K3", "https://picsum.photos/seed/xcool2x/400/400", plays = "6M plays", category = "Energize"),
        Song("10", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luzroja2/400/400", plays = "20M plays", category = "Energize"),
        Song("11", "After Hours (Slowed)", "The Weeknd", "https://picsum.photos/seed/afterhours/400/400", plays = "15M", category = "Sad"),
        Song("12", "Softly", "Karan Aujla", "https://picsum.photos/seed/softly/400/400", plays = "45M", category = "Romance")
    )

    val recentlyAdded = listOf(
        Song("13", "LUZ ROJA (Slowed)", "bxkq", "https://picsum.photos/seed/luz3x/300/300", category = "Energize"),
        Song("14", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luz4x/300/300", plays = "20M plays", category = "Energize"),
        Song("15", "IZ ROJA (Vocal Remix)", "bxkq, Hilal Bilen", "https://picsum.photos/seed/izroja2/300/300", category = "Energize"),
        Song("16", "FUNK ABNORMAL (ULTRA SLOWED)", "DJ V12, SXID", "https://picsum.photos/seed/funk99/300/300", category = "Energize"),
        Song("17", "CALIENTE (Over Slowed)", "Unknown", "https://picsum.photos/seed/caliente99/300/300", category = "Energize"),
        Song("18", "Die For You", "The Weeknd", "https://i.ytimg.com/vi/mHh_pWqOq8E/hqdefault.jpg", plays = "480M", category = "Sad"),
        Song("19", "Peaches", "Justin Bieber", "https://i.ytimg.com/vi/tQ0yjYUFKAE/hqdefault.jpg", plays = "610M", category = "Relax")
    )

    val allSongs: List<Song>
        get() = (quickPicks + coversAndRemixes + recentlyAdded).distinctBy { it.id }

    val moods = listOf("All", "Relax", "Sleep", "Energize", "Sad", "Romance")

    val categories = listOf(
        "Quick picks", "Covers & remixes", "Trending", "Chill", "Workout", "Focus", "Party", "New releases"
    )
}
