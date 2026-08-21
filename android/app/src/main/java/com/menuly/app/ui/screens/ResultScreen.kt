package com.menuly.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menuly.app.R
import com.menuly.app.data.model.WaiterResult
import com.menuly.app.ui.components.GradientButton
import com.menuly.app.ui.components.SurfaceCard
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyGradient
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulySurface
import com.menuly.app.ui.theme.MenulySurface2
import com.menuly.app.ui.theme.MenulyWhite
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    result: WaiterResult,
    menuText: String,
    followUpNote: String,
    isAsking: Boolean,
    onFollowUpChange: (String) -> Unit,
    onAskFollowUp: () -> Unit,
    onBack: () -> Unit,
    onScanAgain: () -> Unit,
) {
    val pick = result.pick
    val sections = result.menuSections.filter { it.items.isNotEmpty() }
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    // After Ask Waiter returns a new pick, jump back to the recommendation
    LaunchedEffect(pick.name, pick.why, isAsking) {
        if (!isAsking) {
            scroll.animateScrollTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            IconButton(onClick = onBack, enabled = !isAsking) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = MenulyWhite)
            }
            Text(
                stringResource(R.string.result_title),
                color = MenulyWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
            )
            if (isAsking) {
                CircularProgressIndicator(
                    color = AccentPink,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(22.dp),
                )
            }
        }

        if (isAsking) {
            Text(
                stringResource(R.string.analyzing_followup),
                color = AccentPink,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.EmojiEvents, null, tint = AccentPink, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.result_pick_heading),
                    color = MenulyWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(MenulyGradient)
                    .padding(1.5.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(21.dp))
                        .background(MenulyBlack)
                        .padding(20.dp),
                ) {
                    Text(
                        pick.name.ifBlank { "—" },
                        color = MenulyWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        listOfNotNull(
                            pick.price,
                            stringResource(R.string.result_fit_score, pick.score),
                        ).joinToString("  ·  "),
                        color = AccentPink,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetaChip("🔥 ${pick.taste.ifBlank { "—" }.take(18)}")
                        MetaChip(
                            stringResource(
                                R.string.meta_spicy,
                                levelLabel(pick.spiciness),
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetaChip(
                            stringResource(
                                R.string.meta_protein,
                                levelLabel(pick.protein),
                            )
                        )
                        MetaChip("💰 ${pick.priceLevel.ifBlank { "$$" }}")
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(pick.why, color = MenulyWhite, fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            if (result.waiterNote.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                SurfaceCard {
                    Text(
                        stringResource(R.string.result_waiter_note),
                        color = AccentPink,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(result.waiterNote, color = MenulyWhite, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            result.runnerUp?.let { ru ->
                Spacer(Modifier.height(16.dp))
                SurfaceCard {
                    Text(stringResource(R.string.result_runner_up), color = MenulyMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        listOfNotNull(ru.name, ru.price).joinToString(" — "),
                        color = MenulyWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    if (ru.why.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(ru.why, color = MenulyMuted, fontSize = 13.sp)
                    }
                }
            }

            result.skip?.let { sk ->
                Spacer(Modifier.height(16.dp))
                SurfaceCard {
                    Text(stringResource(R.string.result_skip), color = MenulyMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(sk.name, color = MenulyWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    if (sk.why.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text("→ ${sk.why}", color = MenulyMuted, fontSize = 13.sp)
                    }
                }
            }

            if (sections.isNotEmpty() || menuText.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    stringResource(R.string.result_scanned_menu),
                    color = MenulyWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MenulySurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (sections.isNotEmpty()) {
                        sections.forEach { section ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (section.title.isNotBlank()) {
                                    Text(
                                        section.title.uppercase(),
                                        color = AccentPink,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.8.sp,
                                    )
                                }
                                section.items.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Text(
                                            item.name,
                                            color = MenulyWhite,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f),
                                            lineHeight = 20.sp,
                                        )
                                        if (!item.price.isNullOrBlank()) {
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                item.price,
                                                color = AccentPink,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(menuText, color = MenulyMuted, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.result_ask_more),
                color = MenulyWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MenulySurface)
                    .padding(14.dp),
            ) {
                BasicTextField(
                    value = followUpNote,
                    onValueChange = onFollowUpChange,
                    enabled = !isAsking,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(color = MenulyWhite, fontSize = 14.sp),
                    cursorBrush = SolidColor(AccentPink),
                    decorationBox = { inner ->
                        if (followUpNote.isEmpty()) {
                            Text(
                                stringResource(R.string.result_ask_placeholder),
                                color = MenulyMuted,
                                fontSize = 14.sp,
                            )
                        }
                        inner()
                    },
                )
            }
            Spacer(Modifier.height(10.dp))
            GradientButton(
                text = stringResource(
                    if (isAsking) R.string.result_asking else R.string.result_ask_button
                ),
                onClick = {
                    scope.launch { scroll.animateScrollTo(0) }
                    onAskFollowUp()
                },
                enabled = !isAsking && followUpNote.trim().length >= 2,
                icon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null,
                        tint = MenulyWhite,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )

            Spacer(Modifier.height(16.dp))
            Text(
                result.disclaimer.ifBlank {
                    stringResource(R.string.result_disclaimer_default)
                },
                color = MenulyMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
            )
            Spacer(Modifier.height(24.dp))
        }

        GradientButton(
            text = stringResource(R.string.result_scan_again),
            onClick = onScanAgain,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun MetaChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MenulySurface2)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(text, color = MenulyWhite, fontSize = 12.sp)
    }
}

@Composable
private fun levelLabel(v: String): String = when (v.lowercase()) {
    "none" -> stringResource(R.string.level_none)
    "low" -> stringResource(R.string.level_low)
    "medium" -> stringResource(R.string.level_medium)
    "high" -> stringResource(R.string.level_high)
    else -> v
}
