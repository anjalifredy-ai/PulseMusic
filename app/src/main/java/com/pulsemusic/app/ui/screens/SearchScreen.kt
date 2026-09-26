package com.pulsemusic.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.MoodChip
import com.pulsemusic.app.ui.components.SongListItem
import com.pulsemusic.app.ui.theme.*

@Composable
fun SearchScreen(
    onSongClick: (Song) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val results = remember(query, selectedCategory) {
        DummyData.allSongs.filter { song ->
            val matchesQuery = query.isBlank() ||
                song.title.contains(query, ignoreCase = true) ||
                song.artist.contains(query, ignoreCase = true)
            val matchesCat = selectedCategory == "All" ||
                song.category.equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCat
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF0D0D12), PulseBlack))
            )
            .padding(top = 12.dp)
    ) {
        Text(
            text = "Search",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Songs, artists, albums...", color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, null, tint = TextSecondary)
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E24),
                unfocusedContainerColor = Color(0xFF1A1A20),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PulsePink,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                MoodChip("All", selectedCategory == "All") { selectedCategory = "All" }
            }
            items(DummyData.moods.filter { it != "All" }) { mood ->
                MoodChip(mood, selectedCategory == mood) { selectedCategory = mood }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (query.isBlank() && selectedCategory == "All") {
            // Categories grid
            Text(
                text = "Browse categories",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DummyData.categories) { cat ->
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        PulsePink.copy(alpha = 0.35f),
                                        PulsePurple.copy(alpha = 0.25f)
                                    )
                                )
                            )
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .clickable { query = cat },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
        ) {
            if (results.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No results found", color = TextMuted, fontSize = 15.sp)
                    }
                }
            } else {
                items(results) { song ->
                    SongListItem(song = song, onClick = { onSongClick(song) })
                }
            }
        }
    }
}
