package com.pulsemusic.app.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.pulsemusic.app.data.Song
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI-facing controller that talks to MusicService via MediaController.
 * Avoids Guava ListenableFuture API surface where possible.
 */
class PlayerController(private val context: Context) {

    private var controller: MediaController? = null
    private val executor = Executors.newSingleThreadExecutor()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    fun connect() {
        val token = SessionToken(context, ComponentName(context, MusicService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            try {
                controller = future.get()
                controller?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }
                })
            } catch (_: Exception) {
                controller = null
            }
        }, executor)
    }

    fun playSong(song: Song, streamUrl: String? = null) {
        _currentSong.value = song
        val url = streamUrl ?: song.streamUrl
        if (url.isNullOrBlank()) {
            _isPlaying.value = true
            return
        }
        val item = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setArtworkUri(Uri.parse(song.coverUrl))
                    .build()
            )
            .build()
        controller?.setMediaItem(item)
        controller?.prepare()
        controller?.play()
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        val c = controller
        if (c == null) {
            _isPlaying.value = !_isPlaying.value
            return
        }
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun release() {
        try {
            controller?.release()
        } catch (_: Exception) { }
        controller = null
        executor.shutdownNow()
    }
}
