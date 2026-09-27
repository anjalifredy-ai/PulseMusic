package com.pulsemusic.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

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
                val req = Request.Builder()
                    .url("$host/streams/$videoId")
                    .header("User-Agent", "PulseMusic/1.0")
                    .header("Accept", "application/json")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val json = JSONObject(body)
                    val audioStreams = json.optJSONArray("audioStreams")
                    if (audioStreams != null && audioStreams.length() > 0) {
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
            } catch (_: Exception) { }
        }
        null
    }

    suspend fun searchPiped(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val q = URLEncoder.encode(query, "UTF-8")
        for (host in PIPED_HOSTS) {
            try {
                val req = Request.Builder()
                    .url("$host/search?q=$q&filter=music_songs")
                    .header("User-Agent", "PulseMusic/1.0")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val arr = JSONArray(body)
                    val out = mutableListOf<Song>()
                    for (i in 0 until arr.length().coerceAtMost(30)) {
                        val o = arr.optJSONObject(i) ?: continue
                        val id = extractId(o)
                        if (id.length != 11) continue
                        val title = o.optString("title").ifBlank { "Track" }
                        val uploader = o.optString("uploader").ifBlank {
                            o.optString("uploaderName").ifBlank { "YouTube" }
                        }
                        val thumb = extractThumb(o, id)
                        out.add(
                            Song(
                                id = id,
                                title = title,
                                artist = uploader,
                                coverUrl = thumb,
                                videoId = id,
                                plays = o.optString("views").takeIf { it.isNotBlank() }
                            )
                        )
                    }
                    // Broader search if music_songs empty
                    if (out.isEmpty()) {
                        val req2 = Request.Builder()
                            .url("$host/search?q=$q&filter=all")
                            .header("User-Agent", "PulseMusic/1.0")
                            .get()
                            .build()
                        client.newCall(req2).execute().use { resp2 ->
                            if (!resp2.isSuccessful) return@use
                            val body2 = resp2.body?.string() ?: return@use
                            val arr2 = JSONArray(body2)
                            for (i in 0 until arr2.length().coerceAtMost(30)) {
                                val o = arr2.optJSONObject(i) ?: continue
                                val type = o.optString("type")
                                if (type == "channel" || type == "playlist") continue
                                val id = extractId(o)
                                if (id.length != 11) continue
                                out.add(
                                    Song(
                                        id = id,
                                        title = o.optString("title").ifBlank { "Track" },
                                        artist = o.optString("uploader").ifBlank { "YouTube" },
                                        coverUrl = extractThumb(o, id),
                                        videoId = id
                                    )
                                )
                            }
                        }
                    }
                    if (out.isNotEmpty()) return@withContext out.distinctBy { it.id }
                }
            } catch (_: Exception) { }
        }
        emptyList()
    }

    suspend fun trending(): List<Song> = withContext(Dispatchers.IO) {
        for (host in PIPED_HOSTS) {
            try {
                val req = Request.Builder()
                    .url("$host/trending?region=US")
                    .header("User-Agent", "PulseMusic/1.0")
                    .get()
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@use
                    val body = resp.body?.string() ?: return@use
                    val arr = JSONArray(body)
                    val out = mutableListOf<Song>()
                    for (i in 0 until arr.length().coerceAtMost(25)) {
                        val o = arr.optJSONObject(i) ?: continue
                        val id = extractId(o)
                        if (id.length != 11) continue
                        val title = o.optString("title")
                        if (title.isBlank()) continue
                        out.add(
                            Song(
                                id = id,
                                title = title,
                                artist = o.optString("uploader").ifBlank { "YouTube" },
                                coverUrl = extractThumb(o, id),
                                videoId = id
                            )
                        )
                    }
                    if (out.isNotEmpty()) return@withContext out.distinctBy { it.id }
                }
            } catch (_: Exception) { }
        }
        emptyList()
    }

    private fun extractId(o: JSONObject): String {
        val fromUrl = o.optString("url")
            .substringAfter("v=")
            .substringAfter("/watch?v=")
            .take(11)
        if (fromUrl.length == 11) return fromUrl
        return o.optString("id").take(11)
    }

    private fun extractThumb(o: JSONObject, id: String): String {
        val thumbs = o.optJSONArray("thumbnails")
        if (thumbs != null && thumbs.length() > 0) {
            val last = thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
            if (!last.isNullOrBlank()) return last.replace("http://", "https://")
            val first = thumbs.optJSONObject(0)?.optString("url")
            if (!first.isNullOrBlank()) return first.replace("http://", "https://")
        }
        val t = o.optString("thumbnail")
        if (t.isNotBlank()) return t.replace("http://", "https://")
        return "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    }
}
