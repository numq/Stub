package io.github.numq.stub.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF43A047),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color.White,

    background = Color(0xFFFDFDFD),
    onBackground = Color(0xFF212121),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),

    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color.White
)

internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF64B5F6),
    onPrimaryContainer = Color.Black,

    secondary = Color(0xFFA5D6A7),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF81C784),
    onSecondaryContainer = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),

    error = Color(0xFFEF9A9A),
    onError = Color.Black,
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color.Black
)