package com.menuly.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Restaurant
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
import com.menuly.app.ui.components.BrandTitle
import com.menuly.app.ui.components.GradientButton
import com.menuly.app.ui.components.GradientPillBadge
import com.menuly.app.ui.components.MoodChip
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulySans
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandTitle(modifier = Modifier.weight(1f), fontSize = 28)
            GradientPillBadge(text = stringResource(R.string.ai_waiter))
            Spacer(Modifier.width(2.dp))
            IconButton(onClick = onLanguage) {
                Icon(
                    Icons.Outlined.Language,
                    contentDescription = stringResource(R.string.change_language),
                    tint = MenulyWhite,
                )
            }
            IconButton(onClick = onHistory) {
                Icon(
                    Icons.Outlined.Folder,
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
                fontFamily = MenulySans,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                fontFamily = MenulySans,
                fontSize = 13.sp,
                lineHeight = 20.sp,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp),
        ) {
            GradientButton(
                text = stringResource(R.string.scan_menu),
                onClick = onScan,
                icon = {
                    Icon(
                        Icons.Outlined.DocumentScanner,
                        contentDescription = null,
                        tint = MenulyWhite,
                        modifier = Modifier.size(22.dp),
                    )
                },
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                NavItem(
                    selected = true,
                    label = stringResource(R.string.nav_waiter),
                    onClick = {},
                ) {
                    Icon(
                        Icons.Outlined.Restaurant,
                        null,
                        tint = if (it) AccentPink else MenulyMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                NavItem(
                    selected = false,
                    label = stringResource(R.string.nav_history),
                    onClick = onHistory,
                ) {
                    Icon(
                        Icons.Outlined.Folder,
                        null,
                        tint = if (it) AccentPink else MenulyMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable (selected: Boolean) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        icon(selected)
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = if (selected) AccentPink else MenulyMuted,
            fontFamily = MenulySans,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}
