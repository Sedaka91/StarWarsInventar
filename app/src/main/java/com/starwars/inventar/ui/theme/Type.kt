package com.starwars.inventar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Monospace mit Laufweite wirkt wie eine Terminal-Anzeige.
// Eine echte Aurebesh-Schrift kann später als Font-Ressource ergänzt werden.
private fun mono(size: Int, weight: FontWeight = FontWeight.Normal, spacing: Double = 0.5) = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = weight,
    fontSize = size.sp,
    letterSpacing = spacing.sp,
    lineHeight = (size + 6).sp
)

val DatapadTypography = Typography(
    headlineMedium = mono(26, FontWeight.Bold, 2.0),
    titleLarge = mono(20, FontWeight.Bold, 1.5),
    titleMedium = mono(16, FontWeight.Medium, 1.0),
    bodyLarge = mono(15),
    bodyMedium = mono(13),
    bodySmall = mono(12),
    labelLarge = mono(14, FontWeight.Bold, 1.5),
    labelMedium = mono(12, FontWeight.Medium, 1.2),
    labelSmall = mono(10, FontWeight.Medium, 1.0)
)
