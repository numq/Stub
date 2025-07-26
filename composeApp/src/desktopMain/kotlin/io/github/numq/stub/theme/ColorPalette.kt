package io.github.numq.stub.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

internal val LightColorPalette = lightColors(
    primary = Color(0xFF90CAF9),       // Light blue for primary elements
    primaryVariant = Color(0xFF42A5F5), // Slightly darker blue for accents
    secondary = Color(0xFF80CBC4),      // Soft teal for secondary elements
    secondaryVariant = Color(0xFF4DB6AC), // Deeper teal for contrast
    background = Color(0xFFF5F5F5),     // Subtle light gray for app background
    surface = Color(0xFFFFFFFF),        // White for card and surface backgrounds
    onPrimary = Color.White,            // White text/icons on primary
    onSecondary = Color.White,          // White text/icons on secondary
    onBackground = Color(0xFF212121),   // Dark gray text/icons on light background
    onSurface = Color(0xFF212121),      // Dark gray text/icons on light surfaces
    error = Color(0xFFEF5350),          // Vibrant red for errors
)

internal val DarkColorPalette = darkColors(
    primary = Color(0xFF1E88E5),       // Rich blue for primary elements
    primaryVariant = Color(0xFF1565C0), // Deeper blue for accents
    secondary = Color(0xFF26A69A),      // Deep teal for secondary elements
    secondaryVariant = Color(0xFF00796B), // Rich greenish-teal for contrast
    background = Color(0xFF121212),     // Dark gray for app background
    surface = Color(0xFF1E1E1E),        // Slightly lighter gray for surfaces
    onPrimary = Color(0xFFBBDEFB),      // Soft blue-gray text/icons on primary
    onSecondary = Color(0xFFB2DFDB),    // Light teal-gray text/icons on secondary
    onBackground = Color(0xFFE0E0E0),   // Light gray text/icons on dark background
    onSurface = Color(0xFFE0E0E0),      // Light gray text/icons on dark surfaces
    error = Color(0xFFEF9A9A),          // Softer red for errors in dark mode
)