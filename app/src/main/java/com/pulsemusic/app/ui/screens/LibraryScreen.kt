package com.pulsemusic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsemusic.app.data.DummyData
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.SongListItem
import com.pulsemusic.app.ui.theme.*

@Composable
fun LibraryScreen(
    onSongClick: (Song) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Text(
                text = "Library",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )
        }

        // Quick access cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LibraryQuickCard(
                    title = "Favorite",
                    icon = Icons.Default.Favorite,
                    gradient = listOf(Color(0xFFFF6B9D), Color(0xFFE91E63)),
                    modifier = Modifier.weight(1f)
                )
                LibraryQuickCard(
                    title = "Followed",
                    icon = Icons.Default.People,
                    gradient = listOf(Color(0xFFFFD54F), Color(0xFFFFB300)),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LibraryQuickCard(
                    title = "Most Played",
                    icon = Icons.Default.History,
                    gradient = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)),
                    modifier = Modifier.weight(1f)
                )
                LibraryQuickCard(
                    title = "Downloaded",
                    icon = Icons.Default.Download,
                    gradient = listOf(Color(0xFF81C784), Color(0xFF388E3C)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Recently Added",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
            )
        }

        items(DummyData.recentlyAdded) { song ->
            SongListItem(song = song, onClick = { onSongClick(song) })
        }
    }
}

@Composable
private fun LibraryQuickCard(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(gradient))
            .clickable { }
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}
