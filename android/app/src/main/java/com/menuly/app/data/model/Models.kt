package com.menuly.app.data.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.menuly.app.R

data class MoodOption(
    val id: String,
    val emoji: String,
    @StringRes val labelRes: Int,
)

object Moods {
    const val DEFAULT_ID = "recommend"

    val all = listOf(
        MoodOption("recommend", "✨", R.string.mood_recommend),
        MoodOption("budget", "💰", R.string.mood_budget),
        MoodOption("delicious", "🤤", R.string.mood_delicious),
        MoodOption("high_protein", "🥩", R.string.mood_high_protein),
        MoodOption("green_diet", "🥗", R.string.mood_green_diet),
    )

    @Composable
    fun labelFor(id: String): String {
        val mood = all.find { it.id == id } ?: return id
        return "${mood.emoji} ${stringResource(mood.labelRes)}"
    }

    fun labelFor(context: android.content.Context, id: String): String {
        val mood = all.find { it.id == id } ?: return id
        return "${mood.emoji} ${context.getString(mood.labelRes)}"
    }
}

data class DishPick(
    val name: String = "",
    val price: String? = null,
    val score: Double = 0.0,
    val tags: List<String> = emptyList(),
    val taste: String = "",
    val spiciness: String = "none",
    val protein: String = "medium",
    val priceLevel: String = "$$",
    val why: String = "",
    val ingredients: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),
)

data class RunnerUp(
    val name: String = "",
    val price: String? = null,
    val why: String = "",
)

data class SkipDish(
    val name: String = "",
    val why: String = "",
)

data class MenuItem(
    val name: String = "",
    val price: String? = null,
)

data class MenuSection(
    val title: String = "",
    val items: List<MenuItem> = emptyList(),
)

data class WaiterResult(
    val menuSections: List<MenuSection> = emptyList(),
    val pick: DishPick = DishPick(),
    val runnerUp: RunnerUp? = null,
    val skip: SkipDish? = null,
    val waiterNote: String = "",
    val disclaimer: String = "",
)

data class AnalyzeResponse(
    val ok: Boolean = false,
    val result: WaiterResult? = null,
    val error: String? = null,
)
