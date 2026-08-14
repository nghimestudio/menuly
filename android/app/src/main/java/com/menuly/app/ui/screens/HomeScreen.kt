package com.menuly.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menuly.app.R
import com.menuly.app.data.model.Moods
import com.menuly.app.ui.MenulyUiState
import com.menuly.app.ui.components.GradientButton
import com.menuly.app.ui.components.MoodChip
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyGradient
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulyWhite

@Composable
fun HomeScreen(
    state: MenulyUiState,
    onSelectMood: (String) -> Unit,
    onScan: () -> Unit,
    onHistory: () -> Unit,
    onLanguage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Menuly",
                color = MenulyWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MenulyGradient)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    stringResource(R.string.ai_waiter),
                    color = MenulyWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onLanguage) {
                Icon(
                    Icons.Default.Language,
                    contentDescription = stringResource(R.string.change_language),
                    tint = MenulyWhite,
                )
            }
            IconButton(onClick = onHistory) {
                Icon(
                    Icons.Default.Folder,
                    contentDescription = stringResource(R.string.history),
                    tint = MenulyWhite,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.mood_question),
                color = MenulyWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(12.dp))

            // 5 moods — wrap so all visible without a duplicate quick button
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Moods.all.chunked(3).forEach { rowMoods ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowMoods.forEach { mood ->
                            MoodChip(
                                emoji = mood.emoji,
                                label = stringResource(mood.labelRes),
                                selected = state.selectedMoodId == mood.id,
                                onClick = { onSelectMood(mood.id) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.home_hint),
                color = MenulyMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp),
        ) {
            GradientButton(
                text = stringResource(R.string.scan_menu),
                onClick = onScan,
                icon = {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = MenulyWhite,
                        modifier = Modifier.size(22.dp),
                    )
                },
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                NavItem(
                    icon = {
                        Icon(
                            Icons.Default.Restaurant,
                            null,
                            tint = AccentPink,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    label = stringResource(R.string.nav_waiter),
                    selected = true,
                    onClick = {},
                )
                NavItem(
                    icon = {
                        Icon(
                            Icons.Default.Folder,
                            null,
                            tint = MenulyMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    },
                    label = stringResource(R.string.nav_history),
                    selected = false,
                    onClick = onHistory,
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        icon()
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = if (selected) AccentPink else MenulyMuted,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
