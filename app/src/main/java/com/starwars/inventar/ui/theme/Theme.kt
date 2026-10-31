package com.starwars.inventar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DatapadColorScheme = darkColorScheme(
    primary = HoloCyan,
    onPrimary = DatapadBg,
    secondary = HoloAmber,
    onSecondary = DatapadBg,
    tertiary = HoloGreen,
    background = DatapadBg,
    onBackground = TextPrimary,
    surface = DatapadSurface,
    onSurface = TextPrimary,
    surfaceVariant = DatapadSurfaceHigh,
    onSurfaceVariant = TextDim,
    surfaceContainerHigh = DatapadSurfaceHigh,
    surfaceContainerHighest = DatapadSurfaceHigh,
    outline = HoloCyan.copy(alpha = 0.5f),
    error = HoloRed
)

@Composable
fun DatapadTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DatapadColorScheme,
        typography = DatapadTypography,
        content = content
    )
}
