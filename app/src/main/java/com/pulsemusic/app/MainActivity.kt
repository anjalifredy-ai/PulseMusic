package com.pulsemusic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pulsemusic.app.data.DummyData
import com.pulsemusic.app.data.Song
import com.pulsemusic.app.ui.components.MiniPlayerBar
import com.pulsemusic.app.ui.screens.*
import com.pulsemusic.app.ui.theme.*

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Library : Screen("library", "Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic)
    data object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulseMusicTheme {
                PulseMusicApp()
            }
        }
    }
}

@Composable
fun PulseMusicApp() {
    val navController = rememberNavController()
    var currentSong by remember { mutableStateOf<Song?>(DummyData.quickPicks.getOrNull(1)) }
    var isPlaying by remember { mutableStateOf(false) }
    var showNowPlaying by remember { mutableStateOf(false) }

    val items = listOf(Screen.Home, Screen.Library, Screen.Search, Screen.Settings)

    if (showNowPlaying && currentSong != null) {
        NowPlayingScreen(
            song = currentSong!!,
            isPlaying = isPlaying,
            onPlayPause = { isPlaying = !isPlaying },
            onBack = { showNowPlaying = false }
        )
    } else {
        Scaffold(
            containerColor = PulseBlack,
            bottomBar = {
                Column {
                    MiniPlayerBar(
                        song = currentSong,
                        isPlaying = isPlaying,
                        onPlayPause = { isPlaying = !isPlaying },
                        onClick = { showNowPlaying = true }
                    )
                    NavigationBar(
                        containerColor = Color(0xF00A0A0A),
                        tonalElevation = 0.dp
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        items.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        if (selected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.label
                                    )
                                },
                                label = { Text(screen.label) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PulsePink,
                                    selectedTextColor = PulsePink,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(onSongClick = {
                        currentSong = it
                        isPlaying = true
                    })
                }
                composable(Screen.Library.route) {
                    LibraryScreen(onSongClick = {
                        currentSong = it
                        isPlaying = true
                    })
                }
                composable(Screen.Search.route) {
                    SearchScreen(onSongClick = {
                        currentSong = it
                        isPlaying = true
                    })
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
