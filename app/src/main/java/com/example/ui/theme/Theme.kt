package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DairyGreenAccent,
    secondary = DairyGreenSecondary,
    tertiary = MilkBlueAccent,
    background = Color(0xFF121B15),
    surface = Color(0xFF1E2822),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE2E8E4),
    onSurface = Color(0xFFE2E8E4)
)

private val LightColorScheme = lightColorScheme(
    primary = DairyGreenPrimary,
    secondary = DairyGreenSecondary,
    tertiary = MilkBlueAccent,
    primaryContainer = DairyGreenLight,
    onPrimaryContainer = Color(0xFF001D36),
    background = MilkCreamBg,
    surface = CardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderColor
)

@Composable
fun DairyKhataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
