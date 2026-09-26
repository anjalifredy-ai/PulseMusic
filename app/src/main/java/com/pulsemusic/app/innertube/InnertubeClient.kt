package com.pulsemusic.app.innertube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Minimal Innertube client for YouTube Music (ANDROID_MUSIC context).
 * Own implementation for PulseMusic — not a fork of SimpMusic/InnerTune.
 */
class InnertubeClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val jsonMedia = "application/json".toMediaType()

    companion object {
        private const val BASE = "https://music.youtube.com/youtubei/v1"
        private const val API_KEY = "AIzaSyAOghZGza2MQSZkY_zfZ370N-PUdXEo8AI"
        private const val CLIENT_NAME = "ANDROID_MUSIC"
        private const val CLIENT_VERSION = "7.27.52"
        private const val USER_AGENT =
            "com.google.android.apps.youtube.music/7.27.52 (Linux; U; Android 14) gzip"

        // Raw string so \s is valid for Regex engine, not Kotlin string escapes
        private val VIDEO_ID_REGEX = Regex(""""videoId"\s*:\s*"([a-zA-Z0-9_-]{11})"""")
    }

    private fun contextBody(additional: JSONObject.() -> Unit = {}): String {
        val root = JSONObject()
        val ctx = JSONObject()
        val clientObj = JSONObject()
            .put("clientName", CLIENT_NAME)
            .put("clientVersion", CLIENT_VERSION)
            .put("hl", "en")
            .put("gl", "US")
            .put("userAgent", USER_AGENT)
        ctx.put("client", clientObj)
        root.put("context", ctx)
        root.additional()
        return root.toString()
    }

    private fun post(path: String, body: String): JSONObject? {
        val url = "$BASE/$path?prettyPrint=false&key=$API_KEY"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("X-Goog-Api-Format-Version", "2")
            .header("Content-Type", "application/json")
            .header("Origin", "https://music.youtube.com")
            .header("Referer", "https://music.youtube.com/")
            .post(body.toRequestBody(jsonMedia))
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val text = response.body?.string() ?: return null
                JSONObject(text)
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun search(query: String): List<InnertubeSong> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val body = contextBody {
            put("query", query)
            put("params", "EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D")
        }

        val json = post("search", body) ?: return@withContext emptyList()
        parseSearchResults(json)
    }

    suspend fun getHomeSuggestions(): List<InnertubeSong> = withContext(Dispatchers.IO) {
        val body = contextBody {
            put("browseId", "FEmusic_charts")
        }
        val json = post("browse", body)
        val fromBrowse = json?.let { parseBrowseSongs(it) }.orEmpty()
        if (fromBrowse.isNotEmpty()) return@withContext fromBrowse
        search("trending songs")
    }

    suspend fun getPlayer(videoId: String): PlayerInfo? = withContext(Dispatchers.IO) {
        val body = contextBody {
            put("videoId", videoId)
            put("contentCheckOk", true)
            put("racyCheckOk", true)
        }
        val json = post("player", body) ?: return@withContext null
        parsePlayer(json, videoId)
    }

    private fun parseSearchResults(json: JSONObject): List<InnertubeSong> {
        val out = mutableListOf<InnertubeSong>()
        try {
            val contents = json
                .optJSONObject("contents")
                ?.optJSONObject("tabbedSearchResultsRenderer")
                ?.optJSONArray("tabs")
                ?: return scrapeAnyVideoIds(json)

            for (i in 0 until contents.length()) {
                val tab = contents.optJSONObject(i) ?: continue
                val sectionList = tab
                    .optJSONObject("tabRenderer")
                    ?.optJSONObject("content")
                    ?.optJSONObject("sectionListRenderer")
                    ?.optJSONArray("contents")
                    ?: continue
                collectSongsFromSections(sectionList, out)
            }
        } catch (_: Exception) {
            return scrapeAnyVideoIds(json)
        }
        if (out.isEmpty()) return scrapeAnyVideoIds(json)
        return out.distinctBy { it.videoId }.take(30)
    }

    private fun parseBrowseSongs(json: JSONObject): List<InnertubeSong> {
        val out = mutableListOf<InnertubeSong>()
        try {
            scrapeAnyVideoIds(json).let { out.addAll(it) }
        } catch (_: Exception) { }
        return out.distinctBy { it.videoId }.take(25)
    }

    private fun collectSongsFromSections(sections: JSONArray, out: MutableList<InnertubeSong>) {
        for (i in 0 until sections.length()) {
            val section = sections.optJSONObject(i) ?: continue
            val musicShelf = section.optJSONObject("musicShelfRenderer")
                ?: section.optJSONObject("musicCarouselShelfRenderer")
            val items = musicShelf?.optJSONArray("contents") ?: continue
            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue
                parseMusicItem(item)?.let { out.add(it) }
            }
        }
    }

    private fun parseMusicItem(item: JSONObject): InnertubeSong? {
        val responsive = item.optJSONObject("musicResponsiveListItemRenderer")
        val twoRow = item.optJSONObject("musicTwoRowItemRenderer")

        val videoId = responsive
            ?.optJSONObject("playlistItemData")
            ?.optString("videoId")
            ?.takeIf { it.isNotBlank() }
            ?: twoRow
                ?.optJSONObject("navigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId")
                ?.takeIf { it.isNotBlank() }
            ?: findVideoId(item)

        if (videoId.isNullOrBlank()) return null

        val title = extractText(
            responsive?.optJSONArray("flexColumns")
                ?.optJSONObject(0)
                ?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                ?.optJSONObject("text")
        ) ?: extractText(twoRow?.optJSONObject("title")) ?: "Unknown"

        val artist = extractText(
            responsive?.optJSONArray("flexColumns")
                ?.optJSONObject(1)
                ?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                ?.optJSONObject("text")
        ) ?: extractText(twoRow?.optJSONObject("subtitle")) ?: "Unknown"

        val thumb = extractThumbnail(responsive) ?: extractThumbnail(twoRow)
            ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

        return InnertubeSong(
            videoId = videoId,
            title = title,
            artist = artist,
            thumbnailUrl = thumb
        )
    }

    private fun extractText(textObj: JSONObject?): String? {
        if (textObj == null) return null
        textObj.optString("simpleText").takeIf { it.isNotBlank() }?.let { return it }
        val runs = textObj.optJSONArray("runs") ?: return null
        val sb = StringBuilder()
        for (i in 0 until runs.length()) {
            sb.append(runs.optJSONObject(i)?.optString("text").orEmpty())
        }
        return sb.toString().ifBlank { null }
    }

    private fun extractThumbnail(renderer: JSONObject?): String? {
        val thumbs = renderer
            ?.optJSONObject("thumbnail")
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")
            ?: renderer?.optJSONObject("thumbnailRenderer")
                ?.optJSONObject("musicThumbnailRenderer")
                ?.optJSONObject("thumbnail")
                ?.optJSONArray("thumbnails")
            ?: return null
        if (thumbs.length() == 0) return null
        return thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
    }

    private fun findVideoId(obj: JSONObject): String? {
        return VIDEO_ID_REGEX.find(obj.toString())?.groupValues?.getOrNull(1)
    }

    private fun scrapeAnyVideoIds(json: JSONObject): List<InnertubeSong> {
        val ids = VIDEO_ID_REGEX.findAll(json.toString())
            .map { it.groupValues[1] }
            .distinct()
            .take(20)
            .toList()
        return ids.map { id ->
            InnertubeSong(
                videoId = id,
                title = "Track $id",
                artist = "YouTube Music",
                thumbnailUrl = "https://i.ytimg.com/vi/$id/hqdefault.jpg"
            )
        }
    }

    private fun parsePlayer(json: JSONObject, videoId: String): PlayerInfo {
        val videoDetails = json.optJSONObject("videoDetails")
        val title = videoDetails?.optString("title") ?: "Unknown"
        val author = videoDetails?.optString("author") ?: "Unknown"
        val length = videoDetails?.optString("lengthSeconds")?.toLongOrNull() ?: 0L

        var streamUrl: String? = null
        val streaming = json.optJSONObject("streamingData")
        val formats = streaming?.optJSONArray("formats")
        if (formats != null) {
            for (i in 0 until formats.length()) {
                val f = formats.optJSONObject(i) ?: continue
                val url = f.optString("url")
                if (url.isNotBlank() && !f.has("signatureCipher") && !f.has("cipher")) {
                    streamUrl = url
                    break
                }
            }
        }
        if (streamUrl == null) {
            val adaptive = streaming?.optJSONArray("adaptiveFormats")
            if (adaptive != null) {
                for (i in 0 until adaptive.length()) {
                    val f = adaptive.optJSONObject(i) ?: continue
                    val mime = f.optString("mimeType")
                    val url = f.optString("url")
                    if (mime.startsWith("audio/") && url.isNotBlank() &&
                        !f.has("signatureCipher") && !f.has("cipher")
                    ) {
                        streamUrl = url
                        break
                    }
                }
            }
        }

        return PlayerInfo(
            videoId = videoId,
            title = title,
            artist = author,
            durationSec = length,
            streamUrl = streamUrl,
            thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        )
    }
}

data class InnertubeSong(
    val videoId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String
)

data class PlayerInfo(
    val videoId: String,
    val title: String,
    val artist: String,
    val durationSec: Long,
    val streamUrl: String?,
    val thumbnailUrl: String
)
