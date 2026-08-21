package com.menuly.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.AccentPurple
import com.menuly.app.ui.theme.MenulyDisplay
import com.menuly.app.ui.theme.MenulyGradient
import com.menuly.app.ui.theme.MenulySans
import com.menuly.app.ui.theme.MenulySurface
import com.menuly.app.ui.theme.MenulySurface2
import com.menuly.app.ui.theme.MenulyWhite
import com.menuly.app.ui.theme.PillShape

@Composable
fun BrandTitle(
    text: String = "Menuly",
    modifier: Modifier = Modifier,
    fontSize: Int = 28,
) {
    Text(
        text = text,
        color = MenulyWhite,
        fontFamily = MenulyDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        letterSpacing = (-0.4).sp,
        modifier = modifier,
    )
}

@Composable
fun GradientPillBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(PillShape)
            .background(MenulyGradient)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            color = MenulyWhite,
            fontFamily = MenulySans,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/** Primary CTA — orange→pink gradient pill (reference “Pro” energy). */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = MenulyWhite,
            modifier = Modifier.size(20.dp),
        )
    },
) {
    val alpha = if (enabled) 1f else 0.45f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MenulyGradient)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = MenulyWhite.copy(alpha = alpha),
                fontFamily = MenulySans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}

/** Secondary CTA — white pill + purple/pink accent text (reference “Create New”). */
@Composable
fun WhitePillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = AccentPurple,
            modifier = Modifier.size(18.dp),
        )
    },
) {
    Row(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(28.dp), clip = false)
            .clip(RoundedCornerShape(28.dp))
            .background(MenulyWhite)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontFamily = MenulySans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = AccentPurple,
        )
    }
}

@Composable
fun MoodChip(
    emoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .then(
                if (selected) Modifier.background(MenulyGradient)
                else Modifier
                    .background(MenulySurface2)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = "$emoji $label",
            color = MenulyWhite,
            fontFamily = MenulySans,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MenulySurface)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MenulyWhite,
        fontFamily = MenulySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
fun GradientCheck(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(RoundedCornerShape(11.dp))
            .then(
                if (selected) Modifier.background(MenulyGradient)
                else Modifier
                    .background(Color.Transparent)
                    .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(11.dp))
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Text(
                "✓",
                color = MenulyWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
