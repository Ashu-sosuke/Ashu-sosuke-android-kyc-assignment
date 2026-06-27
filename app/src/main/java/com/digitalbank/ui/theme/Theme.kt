package com.digitalbank.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Navy900,
    surface = Navy800,
    surfaceVariant = Navy700,
    primary = Electric,
    onPrimary = White90,
    onBackground = White90,
    onSurface = White90,
    onSurfaceVariant = White50,
    error = Rose
)

@Composable
fun DigitalBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
