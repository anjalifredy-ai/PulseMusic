package com.pulsemusic.app.data

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val duration: String = "3:45",
    val plays: String? = null
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
    val quickPicks = listOf(
        Song("1", "Safar", "Bayaan, Sherazam", "https://picsum.photos/seed/safar/300"),
        Song("2", "LUZ ROJA (Slowed)", "bxkq", "https://picsum.photos/seed/luzroja/300", plays = "36M views"),
        Song("3", "X-COOL! (Ultra Slowed)", "tienanh109, HDN, MC K3", "https://picsum.photos/seed/xcool/300"),
        Song("4", "Barsaat", "Banjaare, Roni", "https://picsum.photos/seed/barsaat/300")
    )

    val coversAndRemixes = listOf(
        Song("5", "X-COOL! (Super Slowed)", "tienanh109, HDN, MC K3", "https://picsum.photos/seed/xcool2/400", plays = "6M plays"),
        Song("6", "X-COOL! (Instrumental - Ultra Slowed)", "tienanh109", "https://picsum.photos/seed/xcool3/400", plays = "7.2M"),
        Song("7", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luz2/400", plays = "20M plays")
    )

    val recentlyAdded = listOf(
        Song("8", "LUZ ROJA (Slowed)", "bxkq", "https://picsum.photos/seed/luz3/300"),
        Song("9", "LUZ ROJA (Ultra Slowed)", "bxkq", "https://picsum.photos/seed/luz4/300", plays = "20M plays"),
        Song("10", "IZ ROJA (Vocal Version Remix - Slowed)", "bxkq, Hilal Bilen", "https://picsum.photos/seed/izroja/300"),
        Song("11", "LUZ ROJA (Super Slowed)", "bxkq", "https://picsum.photos/seed/luz5/300"),
        Song("12", "LUZ ROJA (VIP Mix - Slowed)", "bxkq", "https://picsum.photos/seed/luz6/300"),
        Song("13", "FUNK ABNORMAL (ULTRA SLOWED)", "DJ V12, SXID", "https://picsum.photos/seed/funk/300"),
        Song("14", "CALIENTE (Over Slowed)", "Unknown", "https://picsum.photos/seed/caliente/300")
    )

    val moods = listOf("All", "Relax", "Sleep", "Energize", "Sad", "Romance")
}
