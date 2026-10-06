package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val StudioColorScheme = darkColorScheme(
    primary = StudioPurple,
    onPrimary = Color.White,
    secondary = StudioCyan,
    onSecondary = Color.Black,
    tertiary = StudioPink,
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioDarkSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioDarkSurfaceVariant,
    onSurfaceVariant = StudioTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep consistent dark studio palette for pro editor UX
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}
