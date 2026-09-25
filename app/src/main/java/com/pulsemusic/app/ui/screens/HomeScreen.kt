package com.pulsemusic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pulsemusic.app.data.DummyData
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.MoodChip
import com.pulsemusic.app.ui.components.SongCardHorizontal
import com.pulsemusic.app.ui.components.SongListItem
import com.pulsemusic.app.ui.theme.*

@Composable
fun HomeScreen(
    onSongClick: (Song) -> Unit = {}
) {
    var selectedMood by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "PulseMusic",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Good Evening",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // Mood chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DummyData.moods.forEach { mood ->
                    MoodChip(
                        text = mood,
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood }
                    )
                }
            }
        }

        // Welcome back
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PulsePink),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SG", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Welcome back,", color = TextSecondary, fontSize = 13.sp)
                    Text("SUPER GOKU YT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = TextSecondary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        // Quick picks header
        item {
            Text(
                text = "Quick picks",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // Quick picks list
        items(DummyData.quickPicks) { song ->
            SongListItem(song = song, onClick = { onSongClick(song) })
        }

        // Covers and remixes
        item {
            Text(
                text = "Covers and remixes",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DummyData.coversAndRemixes) { song ->
                    SongCardHorizontal(song = song, onClick = { onSongClick(song) })
                }
            }
        }

        // Music videos section placeholder
        item {
            Text(
                text = "Music videos for you",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 28.dp, bottom = 12.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DummyData.coversAndRemixes.take(3)) { song ->
                    Column(modifier = Modifier.width(200.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PulseSurface)
                        ) {
                            AsyncImage(
                                model = song.coverUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(song.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                        Text(song.artist, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}
