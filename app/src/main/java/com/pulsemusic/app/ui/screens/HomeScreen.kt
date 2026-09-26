package com.pulsemusic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
import com.pulsemusic.app.data.DummyData
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.CoverImage
import com.pulsemusic.app.ui.components.MoodChip
import com.pulsemusic.app.ui.components.SongCardHorizontal
import com.pulsemusic.app.ui.components.SongListItem
import com.pulsemusic.app.ui.theme.*

@Composable
fun HomeScreen(
    onSongClick: (Song) -> Unit = {}
) {
    var selectedMood by remember { mutableStateOf("All") }

    val filteredQuick = remember(selectedMood) {
        if (selectedMood == "All") DummyData.quickPicks
        else DummyData.quickPicks.filter { it.category.equals(selectedMood, ignoreCase = true) }
    }
    val filteredCovers = remember(selectedMood) {
        if (selectedMood == "All") DummyData.coversAndRemixes
        else DummyData.coversAndRemixes.filter { it.category.equals(selectedMood, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0D12), PulseBlack, PulseBlack)
                )
            ),
        contentPadding = PaddingValues(bottom = 130.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "PulseMusic",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Good Evening",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PulsePink, PulsePurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Welcome back,", color = TextSecondary, fontSize = 13.sp)
                    Text("SUPER GOKU YT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Notifications, null, tint = TextSecondary)
                }
            }
        }

        item {
            Text(
                text = "Quick picks",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        itemsIndexed(filteredQuick) { index, song ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300 + index * 40)) + slideInVertically { it / 4 }
            ) {
                SongListItem(song = song, onClick = { onSongClick(song) })
            }
        }

        if (filteredQuick.isEmpty()) {
            item {
                Text(
                    text = "No songs in this mood",
                    color = TextMuted,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

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
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCovers) { song ->
                    SongCardHorizontal(song = song, onClick = { onSongClick(song) })
                }
            }
        }

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
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(DummyData.coversAndRemixes.take(4)) { song ->
                    Column(modifier = Modifier.width(210.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(118.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            CoverImage(
                                url = song.coverUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(song.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1, fontWeight = FontWeight.Medium)
                        Text(song.artist, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

private fun tween(duration: Int) = androidx.compose.animation.core.tween<Float>(duration)
