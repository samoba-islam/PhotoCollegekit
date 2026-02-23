package com.samoba.photocollege.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary gradient colors
val GradientStart = Color(0xFF667EEA)
val GradientEnd = Color(0xFF764BA2)
val GradientAccent = Color(0xFFFF6B9D)

// Dark theme colors
val DarkBackground = Color(0xFF0D0D1A)
val DarkSurface = Color(0xFF1A1A2E)
val DarkSurfaceVariant = Color(0xFF252545)
val DarkCard = Color(0xFF16213E)

// Light theme colors
val LightBackground = Color(0xFFF8F9FE)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F1F8)

// Accent colors
val AccentPink = Color(0xFFFF6B9D)
val AccentPurple = Color(0xFF764BA2)
val AccentBlue = Color(0xFF667EEA)
val AccentCyan = Color(0xFF00D4FF)
val AccentGreen = Color(0xFF00D9A5)

// Text colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0C3)
val TextDark = Color(0xFF1A1A2E)
val TextDarkSecondary = Color(0xFF666688)

// Border/Collage colors
val BorderWhite = Color(0xFFFFFFFF)
val BorderBlack = Color(0xFF000000)

// Primary gradient brush
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(GradientStart, GradientEnd, GradientAccent)
)

val CardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1A1A2E).copy(alpha = 0.9f),
        Color(0xFF252545).copy(alpha = 0.7f)
    )
)