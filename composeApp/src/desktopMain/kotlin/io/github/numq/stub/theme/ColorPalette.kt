package io.github.numq.stub.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

internal val LightColorPalette = lightColors(
    primary = Color(0xFF0066CC),
    primaryVariant = Color(0xFF004D99),
    secondary = Color(0xFF4CAF50),
    secondaryVariant = Color(0xFF388E3C),
    background = Color(0xFFF8FAFF),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF333333),
    onSurface = Color(0xFF333333),
    error = Color(0xFFD32F2F),
)

internal val DarkColorPalette = darkColors(
    primary = Color(0xFF5B9DFF),
    primaryVariant = Color(0xFF3A7BD5),
    secondary = Color(0xFF81C784),
    secondaryVariant = Color(0xFF66BB6A),
    background = Color(0xFF121A24),
    surface = Color(0xFF1E2835),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE0E5EC),
    onSurface = Color(0xFFE0E5EC),
    error = Color(0xFFEF5350),
)