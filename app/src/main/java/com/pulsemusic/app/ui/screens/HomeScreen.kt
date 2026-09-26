package com.pulsemusic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsemusic.app.data.DummyData
import com.pulsemusic.app.data.MusicRepository
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
    var quickPicks by remember { mutableStateOf<List<Song>>(DummyData.quickPicks) }
    var covers by remember { mutableStateOf<List<Song>>(DummyData.coversAndRemixes) }
    var loading by remember { mutableStateOf(true) }
    val repo = remember { MusicRepository() }

    LaunchedEffect(Unit) {
        loading = true
        try {
            quickPicks = repo.getHomeQuickPicks()
            covers = repo.getCoversAndRemixes()
        } catch (_: Exception) {
            // keep dummy fallback already set
        }
        loading = false
    }

    val filteredQuick = remember(selectedMood, quickPicks) {
        if (selectedMood == "All") quickPicks
        else quickPicks.filter { it.category.equals(selectedMood, true) }
    }
    val filteredCovers = remember(selectedMood, covers) {
        if (selectedMood == "All") covers
        else covers.filter { it.category.equals(selectedMood, true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D0D12), PulseBlack, PulseBlack))),
        contentPadding = PaddingValues(bottom = 130.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text("PulseMusic", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Own engine · YouTube Music data", color = TextSecondary, fontSize = 13.sp)
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
                    MoodChip(mood, selectedMood == mood) { selectedMood = mood }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PulsePink, PulsePurple))),
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

        if (loading) {
            item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PulsePink)
                }
            }
        }

        item {
            Text(
                "Quick picks",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        itemsIndexed(filteredQuick) { index, song ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(280 + index * 30)) + slideInVertically(tween(280 + index * 30)) { it / 4 }
            ) {
                SongListItem(song = song, onClick = { onSongClick(song) })
            }
        }

        item {
            Text(
                "Covers and remixes",
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
                "Music videos for you",
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
                items(filteredCovers.take(4)) { song ->
                    Column(modifier = Modifier.width(210.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(118.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            CoverImage(song.coverUrl, null, Modifier.fillMaxSize())
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
