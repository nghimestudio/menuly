package com.menuly.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menuly.app.R
import com.menuly.app.data.locale.AppLanguage
import com.menuly.app.data.locale.AppLanguages
import com.menuly.app.ui.components.GradientButton
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyGradient
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulySurface
import com.menuly.app.ui.theme.MenulyWhite
import java.util.Locale

@Composable
fun LanguageSelectScreen(
    selectedTag: String,
    onSelect: (String) -> Unit,
    onContinue: () -> Unit,
) {
    val deviceLang = remember { Locale.getDefault().language }
    val deviceHint = remember {
        when (deviceLang) {
            "vi" -> "Tiếng Việt"
            "zh" -> "中文"
            "ja" -> "日本語"
            "ko" -> "한국어"
            "en" -> "English"
            else -> deviceLang.uppercase(Locale.ROOT)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            text = "Menuly",
            color = MenulyWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.lang_title),
            color = MenulyWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.lang_subtitle),
            color = MenulyMuted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "📱 $deviceHint",
            color = AccentPink,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AppLanguages.all.forEach { lang ->
                LanguageRow(
                    language = lang,
                    selected = selectedTag == lang.tag,
                    onClick = { onSelect(lang.tag) },
                )
            }
        }

        GradientButton(
            text = stringResource(R.string.lang_continue),
            onClick = onContinue,
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

@Composable
private fun LanguageRow(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (selected) Modifier.background(MenulyGradient)
                else Modifier
                    .background(MenulySurface)
                    .border(1.dp, MenulySurface, shape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(language.labelRes),
                color = MenulyWhite,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            if (selected) {
                Text("✓", color = MenulyWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}
