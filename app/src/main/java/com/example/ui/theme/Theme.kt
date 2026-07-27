package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CorporateGold,
    onPrimary = CorporateNavyDark,
    primaryContainer = CorporateNavyLight,
    onPrimaryContainer = CorporateGoldLight,
    secondary = CorporateGoldLight,
    onSecondary = CorporateNavyDark,
    tertiary = AccentBlue,
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = Color(0xFF1E293B),
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF64748B)
)

private val LightColorScheme = lightColorScheme(
    primary = CorporateNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = CorporateNavyDark,
    secondary = CorporateGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF9C3),
    onSecondaryContainer = Color(0xFF713F12),
    tertiary = AccentBlue,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = Color.White,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun JanCoTaxTheme(
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
