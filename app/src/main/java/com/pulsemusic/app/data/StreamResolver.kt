package com.pulsemusic.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Resolve playable audio URL for a YouTube videoId.
 * Uses public Piped instances (same idea many FOSS clients use).
 */
object StreamResolver {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val PIPED_HOSTS = listOf(
        "https://pipedapi.kavin.rocks",
        "https://pipedapi.adminforge.de",
        "https://pipedapi.tokhmi.xyz",
        "https://api.piped.private.coffee"
    )

    suspend fun resolveAudioUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        if (videoId.isBlank() || videoId.length != 11) return@withContext null
        for (host in PIPED_HOSTS) {
            try {
                val url = "$host/streams/$videoId"
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "PulseMusic/1.0")
                    .header("Accept", "application/json")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val json = JSONObject(body)
                    // Prefer audio streams
                    val audioStreams = json.optJSONArray("audioStreams")
                    if (audioStreams != null && audioStreams.length() > 0) {
                        // Highest bitrate first often last or sort by bitrate
                        var bestUrl: String? = null
                        var bestBr = -1
                        for (i in 0 until audioStreams.length()) {
                            val s = audioStreams.optJSONObject(i) ?: continue
                            val u = s.optString("url")
                            val br = s.optInt("bitrate", 0)
                            if (u.isNotBlank() && br >= bestBr) {
                                bestBr = br
                                bestUrl = u
                            }
                        }
                        if (!bestUrl.isNullOrBlank()) return@withContext bestUrl
                    }
                    // Fallback video stream with audio
                    val videoStreams = json.optJSONArray("videoStreams")
                    if (videoStreams != null) {
                        for (i in 0 until videoStreams.length()) {
                            val s = videoStreams.optJSONObject(i) ?: continue
                            if (s.optBoolean("videoOnly", true)) continue
                            val u = s.optString("url")
                            if (u.isNotBlank()) return@withContext u
                        }
                    }
                }
            } catch (_: Exception) {
                // try next host
            }
        }
        null
    }

    suspend fun searchPiped(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        for (host in PIPED_HOSTS) {
            try {
                val url = "$host/search?q=${java.net.URLEncoder.encode(query, "UTF-8")}&filter=music_songs"
                val req = Request.Builder()
                    .url(url)
                    .header("User-Agent", "PulseMusic/1.0")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val arr = org.json.JSONArray(body)
                    val out = mutableListOf<Song>()
                    for (i in 0 until arr.length().coerceAtMost(30)) {
                        val o = arr.optJSONObject(i) ?: continue
                        val type = o.optString("type")
                        if (type.isNotBlank() && type != "stream" && type != "song") continue
                        val id = o.optString("url")
                            .substringAfter("/watch?v=")
                            .substringAfter("watch?v=")
                            .take(11)
                            .ifBlank { o.optString("id").take(11) }
                        if (id.length != 11) continue
                        val title = o.optString("title").ifBlank { "Unknown" }
                        val uploader = o.optString("uploader").ifBlank {
                            o.optString("uploaderName").ifBlank { "YouTube" }
                        }
                        val thumb = o.optJSONArray("thumbnails")
                            ?.optJSONObject(0)
                            ?.optString("url")
                            ?.ifBlank { null }
                            ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg"
                        out.add(
                            Song(
                                id = id,
                                title = title,
                                artist = uploader,
                                coverUrl = thumb.replace("http://", "https://"),
                                videoId = id,
                                plays = o.optString("views").ifBlank { null }
                            )
                        )
                    }
                    if (out.isNotEmpty()) return@withContext out
                }
            } catch (_: Exception) { }
        }
        emptyList()
    }

    suspend fun trending(): List<Song> = withContext(Dispatchers.IO) {
        for (host in PIPED_HOSTS) {
            try {
                // charts / trending music-ish
                val url = "$host/trending?region=US"
                val req = Request.Builder().url(url).header("User-Agent", "PulseMusic/1.0").get().build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val arr = org.json.JSONArray(body)
                    val out = mutableListOf<Song>()
                    for (i in 0 until arr.length().coerceAtMost(25)) {
                        val o = arr.optJSONObject(i) ?: continue
                        val id = o.optString("url").substringAfter("v=").take(11)
                            .ifBlank { o.optString("id").take(11) }
                        if (id.length != 11) continue
                        val title = o.optString("title").ifBlank { continue }
                        val uploader = o.optString("uploader").ifBlank { "YouTube" }
                        val thumb = o.optJSONArray("thumbnails")?.optJSONObject(
                            (o.optJSONArray("thumbnails")?.length() ?: 1) - 1
                        )?.optString("url")
                            ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg"
                        out.add(
                            Song(
                                id = id,
                                title = title,
                                artist = uploader,
                                coverUrl = thumb.replace("http://", "https://"),
                                videoId = id
                            )
                        )
                    }
                    if (out.isNotEmpty()) return@withContext out
                }
            } catch (_: Exception) { }
        }
        emptyList()
    }
}
