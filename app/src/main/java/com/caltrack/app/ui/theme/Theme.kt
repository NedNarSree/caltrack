package com.caltrack.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = CalTrackGreen,
    onPrimary = CalTrackSurface,
    primaryContainer = CalTrackGreenLight,
    onPrimaryContainer = CalTrackGreenDeep,
    background = CalTrackBg,
    onBackground = CalTrackTextPrimary,
    surface = CalTrackSurface,
    onSurface = CalTrackTextPrimary,
    outline = CalTrackBorder,
    outlineVariant = CalTrackBorderSubtle
)

@Composable
fun CalTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
