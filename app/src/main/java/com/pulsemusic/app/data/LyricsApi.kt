package com.pulsemusic.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Real lyrics from LRCLIB public API.
 * Docs: https://lrclib.net/docs
 */
object LyricsApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val LRC_LINE = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\]\\s*(.*)")

    suspend fun fetch(
        trackName: String,
        artistName: String,
        durationSec: Int? = null
    ): List<LyricLine> = withContext(Dispatchers.IO) {
        try {
            val urlBuilder = "https://lrclib.net/api/get".toHttpUrl().newBuilder()
                .addQueryParameter("track_name", trackName.trim())
                .addQueryParameter("artist_name", artistName.trim())
            if (durationSec != null && durationSec in 1..3600) {
                urlBuilder.addQueryParameter("duration", durationSec.toString())
            }

            val request = Request.Builder()
                .url(urlBuilder.build())
                .header("User-Agent", "PulseMusic/1.0.0 (Android)")
                .header("Accept", "application/json")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Fallback: search
                    return@withContext searchAndFetch(trackName, artistName)
                }
                val body = response.body?.string() ?: return@withContext emptyList()
                parseResponse(body)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun searchAndFetch(trackName: String, artistName: String): List<LyricLine> {
        return try {
            val url = "https://lrclib.net/api/search".toHttpUrl().newBuilder()
                .addQueryParameter("track_name", trackName)
                .addQueryParameter("artist_name", artistName)
                .build()
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "PulseMusic/1.0.0 (Android)")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val body = response.body?.string() ?: return emptyList()
                val arr = org.json.JSONArray(body)
                if (arr.length() == 0) return emptyList()
                val first = arr.getJSONObject(0)
                val synced = first.optString("syncedLyrics", "")
                val plain = first.optString("plainLyrics", "")
                if (synced.isNotBlank()) parseSynced(synced)
                else if (plain.isNotBlank()) plainToLines(plain)
                else emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseResponse(json: String): List<LyricLine> {
        val obj = JSONObject(json)
        val synced = obj.optString("syncedLyrics", "")
        val plain = obj.optString("plainLyrics", "")
        return when {
            synced.isNotBlank() -> parseSynced(synced)
            plain.isNotBlank() -> plainToLines(plain)
            else -> emptyList()
        }
    }

    private fun parseSynced(synced: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        synced.lines().forEach { line ->
            val m = LRC_LINE.matcher(line.trim())
            if (m.matches()) {
                val min = m.group(1)!!.toLong()
                val sec = m.group(2)!!.toLong()
                val frac = m.group(3)!!
                val msPart = if (frac.length == 2) frac.toLong() * 10 else frac.toLong()
                val timeMs = min * 60_000 + sec * 1000 + msPart
                val text = m.group(4)?.trim().orEmpty()
                if (text.isNotEmpty()) lines.add(LyricLine(timeMs, text))
            }
        }
        return lines
    }

    private fun plainToLines(plain: String): List<LyricLine> {
        return plain.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { i, text -> LyricLine(i * 3000L, text) }
    }
}
