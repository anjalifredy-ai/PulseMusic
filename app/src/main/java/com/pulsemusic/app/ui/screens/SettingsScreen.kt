package com.pulsemusic.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsemusic.app.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    var normalizeVolume by remember { mutableStateOf(true) }
    var skipSilent by remember { mutableStateOf(false) }
    var savePlaybackState by remember { mutableStateOf(true) }
    var saveLastPlayed by remember { mutableStateOf(true) }
    var killServiceOnExit by remember { mutableStateOf(true) }
    var keepServiceAlive by remember { mutableStateOf(true) }
    var crossfade by remember { mutableStateOf(true) }
    var djStyleTransition by remember { mutableStateOf(true) }
    var translucentNav by remember { mutableStateOf(true) }
    var blurLyrics by remember { mutableStateOf(true) }
    var blurPlayerBg by remember { mutableStateOf(false) }
    var liquidGlass by remember { mutableStateOf(true) }
    var sendListeningData by remember { mutableStateOf(true) }
    var playExplicit by remember { mutableStateOf(true) }
    var keepPlaylistOffline by remember { mutableStateOf(true) }
    var localTracking by remember { mutableStateOf(true) }
    var proxyEnabled by remember { mutableStateOf(true) }
    var helpLyricsDb by remember { mutableStateOf(true) }
    var useAiLyricTranslation by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = PulseBlack,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseBlack)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Settings",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PulseBlack),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item { SectionHeader("Interface") }

            item {
                SettingsClickItem("Theme", "Dark")
                SettingsClickItem("Theme color", "Default")
                SettingsSwitchItem(
                    title = "Translucent bottom navigation bar",
                    subtitle = "You can see the content below the bottom bar",
                    checked = translucentNav,
                    onCheckedChange = { translucentNav = it }
                )
                SettingsSwitchItem(
                    title = "Blur fullscreen lyrics effect",
                    subtitle = "Blurring the background of the Fullscreen lyrics screen effect",
                    checked = blurLyrics,
                    onCheckedChange = { blurLyrics = it }
                )
                SettingsSwitchItem(
                    title = "Blur player background",
                    subtitle = "Blurring the background of the now playing screen effect",
                    checked = blurPlayerBg,
                    onCheckedChange = { blurPlayerBg = it }
                )
                SettingsSwitchItem(
                    title = "Enable liquid glass (BETA)",
                    subtitle = "Enable iOS's liquid glass effect in some position (Android 13+)",
                    checked = liquidGlass,
                    onCheckedChange = { liquidGlass = it }
                )
            }

            item { SectionHeader("Content") }

            item {
                SettingsClickItem("YouTube Account", "Manage your YouTube Accounts")
                SettingsClickItem("Language", "English")
                SettingsClickItem("Content Country", "US")
                SettingsClickItem("Download quality", "High - 256kbps")
                SettingsSwitchItem(
                    title = "Play video for video track instead of audio only",
                    subtitle = "Such as Music Video, Lyrics Video, Podcasts, and more",
                    checked = true,
                    onCheckedChange = {}
                )
                SettingsClickItem("Video Quality", "720p")
                SettingsClickItem("Video download quality", "720p")
                SettingsSwitchItem(
                    title = "Send back listening data to Google",
                    subtitle = "Upload your listening history to YouTube Music server",
                    checked = sendListeningData,
                    onCheckedChange = { sendListeningData = it }
                )
                SettingsSwitchItem(
                    title = "Play explicit content",
                    subtitle = "Enable to play explicit content",
                    checked = playExplicit,
                    onCheckedChange = { playExplicit = it }
                )
                SettingsSwitchItem(
                    title = "Keep showing your YouTube playlist when offline",
                    subtitle = "Save your YT playlist to local and keep to show when offline",
                    checked = keepPlaylistOffline,
                    onCheckedChange = { keepPlaylistOffline = it }
                )
                SettingsSwitchItem(
                    title = "Local tracking listening history",
                    subtitle = "Log your listening history to local database",
                    checked = localTracking,
                    onCheckedChange = { localTracking = it }
                )
                SettingsSwitchItem(
                    title = "Proxy",
                    subtitle = "Using Proxy to bypass country content blocking",
                    checked = proxyEnabled,
                    onCheckedChange = { proxyEnabled = it }
                )
                SettingsClickItem("Proxy type", "HTTP")
                SettingsClickItem("Proxy host", "")
                SettingsClickItem("Proxy port", "8000")
            }

            item { SectionHeader("Playback") }

            item {
                SettingsSwitchItem(
                    title = "Normalize Volume",
                    subtitle = "Balance media loudness",
                    checked = normalizeVolume,
                    onCheckedChange = { normalizeVolume = it }
                )
                SettingsSwitchItem(
                    title = "Skip Silent",
                    subtitle = "Skip no music part",
                    checked = skipSilent,
                    onCheckedChange = { skipSilent = it }
                )
                SettingsClickItem("Open system equalizer", "Use your system equalizer")
                SettingsSwitchItem(
                    title = "Save Playback State",
                    subtitle = "Save shuffle and repeat mode",
                    checked = savePlaybackState,
                    onCheckedChange = { savePlaybackState = it }
                )
                SettingsSwitchItem(
                    title = "Save Last Played",
                    subtitle = "Save last played track and queue",
                    checked = saveLastPlayed,
                    onCheckedChange = { saveLastPlayed = it }
                )
                SettingsSwitchItem(
                    title = "Kill service on exit",
                    subtitle = "Stop background music player when exiting app",
                    checked = killServiceOnExit,
                    onCheckedChange = { killServiceOnExit = it }
                )
                SettingsSwitchItem(
                    title = "Keep service alive",
                    subtitle = "Keep music player service alive to avoid being killed by system",
                    checked = keepServiceAlive,
                    onCheckedChange = { keepServiceAlive = it }
                )
                SettingsSwitchItem(
                    title = "Crossfade (BETA)",
                    subtitle = "Smoothly transition between songs",
                    checked = crossfade,
                    onCheckedChange = { crossfade = it }
                )
                SettingsClickItem("Crossfade Duration", "15s")
                SettingsSwitchItem(
                    title = "DJ-Style Transition",
                    subtitle = "",
                    checked = djStyleTransition,
                    onCheckedChange = { djStyleTransition = it }
                )
            }

            item { SectionHeader("Lyrics & AI") }

            item {
                SettingsClickItem("Translation Language", "en")
                SettingsClickItem("YouTube Subtitle Translation Language", "en")
                SettingsSwitchItem(
                    title = "Help PulseMusic lyrics build the lyrics database",
                    subtitle = "Send your saved lyrics to PulseMusic lyrics database",
                    checked = helpLyricsDb,
                    onCheckedChange = { helpLyricsDb = it }
                )
                SettingsClickItem("Contributor name", "Anonymous")
                SettingsClickItem("Contributor email", "Anonymous")
                SettingsClickItem("AI provider", "Gemini")
                SettingsClickItem("Your AI API Key", "XXXXXXXXXX")
                SettingsClickItem("Custom AI model ID", "gemini-2.0-flash")
                SettingsSwitchItem(
                    title = "Use AI Lyric Translation",
                    subtitle = "Enable lyrics translation using AI",
                    checked = useAiLyricTranslation,
                    onCheckedChange = { useAiLyricTranslation = it }
                )
            }

            item { SectionHeader("Storage") }

            item {
                SettingsClickItem("Player Cache", "80 MB")
                SettingsClickItem("Downloaded Cache", "0 MB")
                SettingsClickItem("Thumbnail Cache", "31 MB")
                SettingsClickItem("Spotify Canvas Cache", "0 MB")
                SettingsClickItem("Limit Player Cache", "∞")
            }

            item { SectionHeader("SponsorBlock") }

            item {
                SettingsClickItem(
                    title = "Select skip-segment behavior",
                    subtitle = "What segments will be skipped"
                )
                Text(
                    text = "SponsorBlock is a crowd-sourced system for skipping annoying parts of YouTube videos.\nMore information: https://sponsor.ajay.app/\nIn PulseMusic, SponsorBlock is only available when the device is online",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            item { SectionHeader("About") }

            item {
                SettingsClickItem("Version", "1.0.0-alpha")
                SettingsClickItem("GitHub", "anjalifredy-ai/PulseMusic")
                SettingsClickItem("Open Source Licenses", "")
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = PulsePink,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF26A69A),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF424242)
            )
        )
    }
}

@Composable
private fun SettingsClickItem(
    title: String,
    subtitle: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted
        )
    }
}
