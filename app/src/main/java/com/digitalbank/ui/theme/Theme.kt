package com.digitalbank.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF000000),      // pure black
    surface = Color(0xFF121212),         // dark surface for cards
    surfaceVariant = Color(0xFF1E1E1E),  // elevated surfaces
    primary = Electric,
    onPrimary = White90,
    onBackground = White90,
    onSurface = White90,
    onSurfaceVariant = White50,
    error = Rose
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFF4F6FA),       // light neutral background
    surface = Color(0xFFFFFFFF),          // clean white card surfaces
    surfaceVariant = Color(0xFFE2E7F0),   // soft light gray details
    primary = ElectricDim,                // primary blue with optimized contrast
    onPrimary = Color.White,
    onBackground = Color(0xFF0A1628),     // dark text
    onSurface = Color(0xFF0A1628),        // dark text
    onSurfaceVariant = Color(0xFF64748B),  // secondary slate gray text
    error = Rose
)

@Composable
fun DigitalBankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
