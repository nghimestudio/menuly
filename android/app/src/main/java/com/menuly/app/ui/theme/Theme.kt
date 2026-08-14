package com.menuly.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val MenulyBlack = Color(0xFF000000)
val MenulySurface = Color(0xFF1C1C1E)
val MenulySurface2 = Color(0xFF2C2C2E)
val MenulyMuted = Color(0xFF8E8E93)
val MenulyWhite = Color(0xFFFFFFFF)
val MenulyText = MenulyWhite
val AccentOrange = Color(0xFFFF8A3D)
val AccentPink = Color(0xFFFF2D92)
val AccentPurple = Color(0xFFA855F7)

val MenulyGradient = Brush.horizontalGradient(
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
val CardShape = RoundedCornerShape(20.dp)
val InputShape = RoundedCornerShape(18.dp)

@Composable
fun MenulyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
            ),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        ),
        content = content,
    )
}

fun Modifier.menulyGradientBackground(): Modifier =
    this.background(MenulyGradient)
