package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Forced Dark Theme for cinematic premium aesthetic
private val MovaDarkColorScheme = darkColorScheme(
    primary = MovaPrimaryRed,
    secondary = MovaStarGold,
    tertiary = MovaSparkleAqua,
    background = MovaAbyssBlack,
    surface = MovaSurfaceDark,
    onPrimary = Color.White,
    onSecondary = MovaAbyssBlack,
    onTertiary = MovaAbyssBlack,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1F2A),
    onSurfaceVariant = MovaLightGrey
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode for theater movie screen ambient style
    dynamicColor: Boolean = false, // Disable dynamic colors to keep layout stylized
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MovaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
