package com.menuly.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menuly.app.R
import com.menuly.app.ui.theme.AccentOrange
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.AccentPurple
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulySurface
import com.menuly.app.ui.theme.MenulyWhite

@Composable
fun AnalyzingScreen(message: String) {
    val infinite = rememberInfiniteTransition(label = "wait")
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val shimmer by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack)
            .statusBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size((88 * pulse).dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(AccentOrange, AccentPink, AccentPurple)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text("🍽️", fontSize = 40.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            stringResource(R.string.analyzing_title),
            color = MenulyWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            message.ifBlank { stringResource(R.string.analyzing_default) },
            color = MenulyMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(MenulySurface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f + shimmer * 0.55f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentOrange, AccentPink, AccentPurple)
                        )
                    ),
            )
        }
    }
}
