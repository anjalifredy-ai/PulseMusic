package com.pulsemusic.app.ui.screens

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.pulsemusic.app.data.LyricLine
import com.pulsemusic.app.data.MusicRepository
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun NowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onBack: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.05f) }
    var showLyrics by remember { mutableStateOf(false) }
    var lyrics by remember { mutableStateOf<List<LyricLine>>(emptyList()) }
    var lyricsLoading by remember { mutableStateOf(false) }
    var activeLyricIndex by remember { mutableIntStateOf(0) }
    var themeColor by remember { mutableStateOf(Color(0xFF3A0A22)) }
    val repo = remember { MusicRepository() }
    val context = LocalContext.current

    // Dynamic theme from cover banner
    LaunchedEffect(song.coverUrl) {
        try {
            val req = ImageRequest.Builder(context)
                .data(song.coverUrl)
                .allowHardware(false)
                .build()
            val result = context.imageLoader.execute(req)
            if (result is SuccessResult) {
                val bmp = (result.drawable as? BitmapDrawable)?.bitmap
                if (bmp != null) {
                    val palette = withContext(Dispatchers.Default) {
                        Palette.from(bmp).generate()
                    }
                    val swatch = palette.vibrantSwatch
                        ?: palette.darkVibrantSwatch
                        ?: palette.mutedSwatch
                        ?: palette.dominantSwatch
                    if (swatch != null) {
                        themeColor = Color(swatch.rgb)
                    }
                }
            }
        } catch (_: Exception) { }
    }

    val bgTop by animateColorAsState(themeColor.copy(alpha = 0.85f), label = "bgTop")
    val accent by animateColorAsState(themeColor, label = "accent")

    LaunchedEffect(song.id) {
        lyricsLoading = true
        lyrics = repo.getLyrics(song)
        lyricsLoading = false
        progress = 0.05f
        activeLyricIndex = 0
    }

    LaunchedEffect(isPlaying, song.id, lyrics) {
        if (!isPlaying || lyrics.isEmpty()) return@LaunchedEffect
        val maxTime = lyrics.lastOrNull()?.timeMs?.coerceAtLeast(30_000L) ?: 30_000L
        while (true) {
            delay(400)
            progress = (progress + 0.005f).coerceAtMost(1f)
            val approxMs = (progress * maxTime).toLong()
            activeLyricIndex = lyrics.indexOfLast { it.timeMs <= approxMs }.coerceAtLeast(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(bgTop, Color(0xFF12080E), PulseBlack)
                )
            )
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NOW PLAYING", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                Text(
                    text = song.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = { showLyrics = !showLyrics }) {
                Icon(
                    imageVector = if (showLyrics) Icons.Default.Album else Icons.Default.MusicNote,
                    contentDescription = "Lyrics",
                    tint = if (showLyrics) accent else TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = showLyrics,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "player_lyrics",
                modifier = Modifier.fillMaxSize()
            ) { lyricsMode ->
                if (lyricsMode) {
                    when {
                        lyricsLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = accent)
                            }
                        }
                        lyrics.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No lyrics found", color = TextMuted)
                            }
                        }
                        else -> {
                            LyricsPanel(
                                lyrics = lyrics,
                                activeIndex = activeLyricIndex,
                                accent = accent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Banner / cover
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(song.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Text(
                            text = song.title,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(song.artist, color = TextSecondary, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Add, null, tint = TextSecondary)
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.FavoriteBorder, null, tint = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        Slider(
            value = progress,
            onValueChange = { progress = it },
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color(0xFF333333)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime((progress * 210).toInt()), color = TextMuted, fontSize = 12.sp)
            Text(song.duration, color = TextMuted, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Shuffle, null, tint = TextSecondary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipPrevious, null, tint = TextPrimary, modifier = Modifier.size(36.dp))
            }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(accent, accent.copy(alpha = 0.7f))))
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.SkipNext, null, tint = TextPrimary, modifier = Modifier.size(36.dp))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Repeat, null, tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun LyricsPanel(
    lyrics: List<LyricLine>,
    activeIndex: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(activeIndex) {
        if (activeIndex in lyrics.indices) {
            listState.animateScrollToItem(activeIndex)
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 40.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            val active = index == activeIndex
            Text(
                text = line.text.ifBlank { "···" },
                color = if (active) Color.White else TextMuted,
                fontSize = if (active) 22.sp else 16.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
