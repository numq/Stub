package io.github.numq.stub.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

internal val LightColorPalette = lightColors(
    primary = Color(0xFF1E88E5),
    primaryVariant = Color(0xFF1565C0),
    secondary = Color(0xFF43A047),
    secondaryVariant = Color(0xFF2E7D32),
    background = Color(0xFFFDFDFD),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    error = Color(0xFFD32F2F)
)

internal val DarkColorPalette = darkColors(
    primary = Color(0xFF90CAF9),
    primaryVariant = Color(0xFF64B5F6),
    secondary = Color(0xFFA5D6A7),
    secondaryVariant = Color(0xFF81C784),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF9A9A)
)