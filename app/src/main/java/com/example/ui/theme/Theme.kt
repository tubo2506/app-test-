package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VibrantPink,
    onPrimary = Color.White,
    primaryContainer = VibrantPinkLight,
    onPrimaryContainer = VibrantPinkDark,
    secondary = VibrantMint,
    onSecondary = Color.White,
    secondaryContainer = VibrantGreenBg,
    onSecondaryContainer = VibrantGreenDark,
    tertiary = VibrantPeachEnd,
    background = VibrantBg,
    surface = VibrantCardBg,
    onBackground = VibrantTextPrimary,
    onSurface = VibrantTextPrimary,
    surfaceVariant = VibrantPinkSubtle,
    onSurfaceVariant = VibrantTextSecondary,
    outline = VibrantBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use the fresh, bright, crisp light gelato color scheme
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
