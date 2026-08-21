package com.menuly.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val MenulyBlack = Color(0xFF000000)
val MenulySurface = Color(0xFF141416)
val MenulySurface2 = Color(0xFF1C1C1E)
val MenulyMuted = Color(0xFF9A9AA0)
val MenulyWhite = Color(0xFFFFFFFF)
val MenulyText = MenulyWhite

/** Reference-style accents: orange → hot pink (+ soft purple for secondary). */
val AccentOrange = Color(0xFFFF8A3D)
val AccentPink = Color(0xFFFF2D92)
val AccentPurple = Color(0xFFB06BFF)

val MenulyGradient = Brush.horizontalGradient(
    colors = listOf(AccentOrange, AccentPink)
)

val MenulyGradientFull = Brush.horizontalGradient(
    colors = listOf(AccentOrange, AccentPink, AccentPurple)
)

val MenulyGradientVertical = Brush.verticalGradient(
    colors = listOf(AccentOrange.copy(alpha = 0.35f), Color.Transparent)
)

fun menulyDiagonalGradient(): Brush = Brush.linearGradient(
    colors = listOf(AccentOrange, AccentPink, AccentPurple),
    start = Offset.Zero,
    end = Offset(800f, 200f),
)

private val DarkColors = darkColorScheme(
    primary = AccentPink,
    onPrimary = MenulyWhite,
    secondary = AccentPurple,
    background = MenulyBlack,
    onBackground = MenulyWhite,
    surface = MenulySurface,
    onSurface = MenulyWhite,
    onSurfaceVariant = MenulyMuted,
)

val PillShape = RoundedCornerShape(50)
val CardShape = RoundedCornerShape(22.dp)
val InputShape = RoundedCornerShape(18.dp)

private val MenulyTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MenulyDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
        color = MenulyWhite,
    ),
    displayMedium = TextStyle(
        fontFamily = MenulyDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp,
        color = MenulyWhite,
    ),
    headlineMedium = TextStyle(
        fontFamily = MenulyDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = MenulyWhite,
    ),
    titleLarge = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = MenulyWhite,
    ),
    titleMedium = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = MenulyWhite,
    ),
    bodyLarge = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = MenulyWhite,
    ),
    bodyMedium = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = MenulyMuted,
    ),
    bodySmall = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = MenulyMuted,
    ),
    labelLarge = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = MenulyWhite,
    ),
    labelMedium = TextStyle(
        fontFamily = MenulySans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = MenulyMuted,
    ),
)

@Composable
fun MenulyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = MenulyTypography,
        content = content,
    )
}

fun Modifier.menulyGradientBackground(): Modifier =
    this.background(MenulyGradient)
