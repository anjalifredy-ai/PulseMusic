package com.pulsemusic.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))
                )
            )
            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(18.dp))
            .padding(12.dp),
        content = content
    )
}

@Composable
fun CoverImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = TextMuted, modifier = Modifier.size(32.dp))
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(PulsePink.copy(alpha = 0.4f), PulsePurple.copy(alpha = 0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
            }
        }
    )
}

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, tween(120))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoverImage(
            url = song.coverUrl,
            contentDescription = song.title,
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist + (song.plays?.let { " · $it" } ?: ""),
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SongCardHorizontal(
    song: Song,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, tween(120))

    Column(
        modifier = modifier
            .width(148.dp)
            .scale(scale)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        CoverImage(
            url = song.coverUrl,
            contentDescription = song.title,
            modifier = Modifier
                .size(148.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = song.title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            color = TextSecondary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MoodChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) {
        Brush.horizontalGradient(listOf(PulsePink, PulseMagenta))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF2A2A2A), Color(0xFF222222)))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(bg)
            .border(
                1.dp,
                if (selected) Color.Transparent else Color(0x33FFFFFF),
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun MiniPlayerBar(
    song: Song?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xF21A1A1A), Color(0xF22A0A1A), Color(0xF21A1A1A))
                )
            )
            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CoverImage(
                url = song.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(PulsePink, PulseMagenta))
                    )
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
