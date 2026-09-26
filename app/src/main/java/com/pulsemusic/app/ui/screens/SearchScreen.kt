package com.pulsemusic.app.ui.screens

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
import com.pulsemusic.app.data.MusicRepository
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.MoodChip
import com.pulsemusic.app.ui.components.SongListItem
import com.pulsemusic.app.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onSongClick: (Song) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var results by remember { mutableStateOf<List<Song>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val repo = remember { MusicRepository() }
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    fun runSearch(q: String) {
        searchJob?.cancel()
        searchJob = scope.launch {
            if (q.isBlank()) {
                results = emptyList()
                loading = false
                return@launch
            }
            delay(400)
            loading = true
            error = null
            try {
                results = repo.search(q)
            } catch (e: Exception) {
                error = e.message ?: "Search failed"
                results = emptyList()
            }
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D0D12), PulseBlack)))
            .padding(top = 12.dp)
    ) {
        Text(
            text = "Search",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = "YouTube Music (own Innertube)",
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = query,
            onValueChange = {
                query = it
                runSearch(it)
            },
            placeholder = { Text("Songs, artists...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
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

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { MoodChip("All", selectedCategory == "All") { selectedCategory = "All" } }
            items(DummyData.moods.filter { it != "All" }) { mood ->
                MoodChip(mood, selectedCategory == mood) { selectedCategory = mood }
            }
        }

        if (query.isBlank()) {
            Text(
                text = "Browse categories",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
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
                            .clickable {
                                query = cat
                                runSearch(cat)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PulsePink)
                }
            }
            error != null -> {
                Text(error!!, color = TextMuted, modifier = Modifier.padding(20.dp))
            }
            else -> {
                val filtered = results
                LazyColumn(contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)) {
                    if (query.isNotBlank() && filtered.isEmpty()) {
                        item {
                            Text("No results", color = TextMuted, modifier = Modifier.padding(20.dp))
                        }
                    }
                    items(filtered) { song ->
                        SongListItem(song = song, onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}
