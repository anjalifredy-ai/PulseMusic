package com.pulsemusic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.CoverImage
import com.pulsemusic.app.ui.theme.*

@Composable
fun NowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onBack: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.18f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3A0A22), Color(0xFF1A0A12), PulseBlack)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.KeyboardArrowDown, "Close", tint = TextPrimary, modifier = Modifier.size(32.dp))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                    Text(
                        text = "\"${song.title}\" Radio",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, null, tint = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            CoverImage(
                url = song.coverUrl,
                contentDescription = song.title,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(22.dp))
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, "Add", tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FavoriteBorder, "Like", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = progress,
                onValueChange = { progress = it },
                colors = SliderDefaults.colors(
                    thumbColor = PulsePink,
                    activeTrackColor = PulsePink,
                    inactiveTrackColor = Color(0xFF333333)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0:38", color = TextMuted, fontSize = 12.sp)
                Text(song.duration, color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(PulsePink, PulseMagenta)))
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
}
