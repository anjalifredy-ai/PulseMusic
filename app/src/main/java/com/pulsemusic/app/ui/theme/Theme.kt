package com.pulsemusic.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PulseDarkColorScheme = darkColorScheme(
    primary = PulsePink,
    onPrimary = Color.White,
    secondary = PulsePurple,
    onSecondary = Color.White,
    tertiary = PulseAccent,
    background = PulseBlack,
    onBackground = TextPrimary,
    surface = PulseDark,
    onSurface = TextPrimary,
    surfaceVariant = PulseSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun PulseMusicTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = PulseDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
