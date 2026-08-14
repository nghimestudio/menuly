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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.menuly.app.data.db.HistoryEntity
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulySurface
import com.menuly.app.ui.theme.MenulyWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyItems: List<HistoryEntity>,
    onBack: () -> Unit,
    onOpen: (HistoryEntity) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val fmt = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MenulyWhite,
                )
            }
            Text(
                text = stringResource(R.string.history),
                color = MenulyWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
        }

        if (historyItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    color = MenulyMuted,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = historyItems,
                    key = { it.id },
                ) { entry ->
                    HistoryRow(
                        entry = entry,
                        dateLabel = fmt.format(Date(entry.createdAt)),
                        onOpen = onOpen,
                        onDelete = onDelete,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntity,
    dateLabel: String,
    onOpen: (HistoryEntity) -> Unit,
    onDelete: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MenulySurface)
            .clickable { onOpen(entry) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.pickName,
                color = MenulyWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = listOfNotNull(
                    entry.pickPrice,
                    String.format("%.1f/10", entry.score),
                    entry.mood,
                ).joinToString(" · "),
                color = AccentPink,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dateLabel,
                color = MenulyMuted,
                fontSize = 11.sp,
            )
        }
        IconButton(onClick = { onDelete(entry.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MenulyMuted,
            )
        }
    }
}
