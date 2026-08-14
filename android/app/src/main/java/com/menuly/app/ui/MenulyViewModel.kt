package com.menuly.app.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.menuly.app.R
import com.menuly.app.data.AppContainer
import com.menuly.app.data.db.HistoryEntity
import com.menuly.app.data.locale.AppLanguages
import com.menuly.app.data.model.Moods
import com.menuly.app.data.model.WaiterResult
import com.menuly.app.ocr.MenuOcr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MenulyUiState(
    val selectedMoodId: String = Moods.DEFAULT_ID,
    val customNote: String = "",
    val menuText: String = "",
    val isScanning: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isAskingFollowUp: Boolean = false,
    val analyzeMessage: String = "",
    val result: WaiterResult? = null,
    val error: String? = null,
    val scanEpoch: Int = 0,
)

class MenulyViewModel(
    application: Application,
    private val container: AppContainer,
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(MenulyUiState())
    val ui: StateFlow<MenulyUiState> = _ui.asStateFlow()

    val history = container.historyDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val languageTag = container.localeStore.languageTag
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val hasChosenLanguage = container.localeStore.hasChosenLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun selectMood(id: String) {
        _ui.update { it.copy(selectedMoodId = id, error = null) }
    }

    fun setCustomNote(note: String) {
        _ui.update { it.copy(customNote = note.take(500)) }
    }

    fun clearError() {
        _ui.update { it.copy(error = null) }
    }

    fun setLanguage(tag: String, markChosen: Boolean = true) {
        viewModelScope.launch {
            container.localeStore.setLanguage(tag, markChosen)
        }
    }

    fun startScanSession() {
        _ui.update {
            it.copy(
                isScanning = true,
                isAnalyzing = false,
                isAskingFollowUp = false,
                menuText = "",
                customNote = "",
                result = null,
                error = null,
                analyzeMessage = "",
                scanEpoch = it.scanEpoch + 1,
            )
        }
    }

    /** Capture done → show thinking immediately; OCR + API in background */
    fun onFrameCaptured(bitmap: Bitmap) {
        _ui.update {
            it.copy(
                isScanning = false,
                isAnalyzing = true,
                analyzeMessage = getApplication<Application>().getString(R.string.analyzing_api),
                error = null,
                result = null,
            )
        }
        viewModelScope.launch {
            try {
                val text = withContext(Dispatchers.Default) {
                    MenuOcr.recognize(bitmap)
                }
                if (!bitmap.isRecycled) bitmap.recycle()
                val cleaned = text.trim()
                if (cleaned.length < 10) {
                    _ui.update {
                        it.copy(
                            isAnalyzing = false,
                            isScanning = true,
                            error = getApplication<Application>().getString(R.string.error_ocr_short),
                            scanEpoch = it.scanEpoch + 1,
                        )
                    }
                    return@launch
                }
                _ui.update { it.copy(menuText = cleaned) }
                analyze(cleaned, followUp = false)
            } catch (e: Exception) {
                if (!bitmap.isRecycled) bitmap.recycle()
                _ui.update {
                    it.copy(
                        isAnalyzing = false,
                        isScanning = true,
                        error = e.message
                            ?: getApplication<Application>().getString(R.string.error_analyze_failed),
                        scanEpoch = it.scanEpoch + 1,
                    )
                }
            }
        }
    }

    fun onScanComplete(text: String) {
        val cleaned = text.trim()
        if (cleaned.length < 10) {
            _ui.update {
                it.copy(
                    isScanning = false,
                    error = getApplication<Application>().getString(R.string.error_ocr_short),
                )
            }
            return
        }
        _ui.update { it.copy(menuText = cleaned, isScanning = false) }
        analyze(cleaned, followUp = false)
    }

    fun askFollowUp() {
        val note = _ui.value.customNote.trim()
        if (note.length < 2) return

        val menu = menuTextForApi(_ui.value)
        if (menu.length < 10) {
            _ui.update {
                it.copy(error = getApplication<Application>().getString(R.string.error_no_menu))
            }
            return
        }
        analyze(menu, followUp = true)
    }

    /** Prefer AI-formatted sections; fall back to raw OCR. */
    private fun menuTextForApi(state: MenulyUiState): String {
        val sections = state.result?.menuSections.orEmpty().filter { it.items.isNotEmpty() }
        if (sections.isNotEmpty()) {
            return sections.joinToString("\n\n") { section ->
                buildString {
                    if (section.title.isNotBlank()) appendLine(section.title.uppercase())
                    section.items.forEach { item ->
                        val price = item.price?.takeIf { it.isNotBlank() }
                        if (price != null) appendLine("${item.name} — $price")
                        else appendLine(item.name)
                    }
                }.trim()
            }
        }
        return state.menuText
    }

    private fun analyze(menuText: String, followUp: Boolean) {
        val app = getApplication<Application>()
        val moodId = _ui.value.selectedMoodId
        val moodLabel = Moods.labelFor(app, moodId)
        val note = _ui.value.customNote
        val mode = when {
            followUp -> "recommend"
            moodId == "recommend" && note.isBlank() -> "surprise"
            moodId == "budget" -> "recommend"
            moodId == "green_diet" -> "recommend"
            else -> "recommend"
        }

        val moodHint = when {
            followUp -> "$moodLabel | Guest follow-up: $note"
            moodId == "budget" -> "$moodLabel — cheapest / best value"
            moodId == "green_diet" -> "$moodLabel — plant-forward, veggies, lighter eco-friendly options"
            moodId == "delicious" -> "$moodLabel — prioritize taste / signature dishes"
            moodId == "high_protein" -> "$moodLabel — maximize protein"
            else -> moodLabel
        }

        _ui.update {
            it.copy(
                isScanning = false,
                isAnalyzing = !followUp,
                isAskingFollowUp = followUp,
                analyzeMessage = app.getString(
                    if (followUp) R.string.analyzing_followup else R.string.analyzing_api
                ),
                error = null,
            )
        }

        viewModelScope.launch {
            try {
                val result = container.api.analyze(
                    menuText = menuText,
                    mood = moodHint,
                    customNote = note,
                    mode = mode,
                    language = AppLanguages.apiLanguageCode(),
                )
                container.historyDao.insert(
                    HistoryEntity(
                        mood = if (note.isBlank()) moodLabel else "$moodLabel · $note",
                        menuPreview = menuText.take(200),
                        pickName = result.pick.name,
                        pickPrice = result.pick.price,
                        score = result.pick.score,
                        why = result.pick.why,
                        waiterNote = result.waiterNote,
                        resultJson = container.api.toJson(result),
                    )
                )
                _ui.update {
                    it.copy(
                        isAnalyzing = false,
                        isAskingFollowUp = false,
                        result = result,
                        analyzeMessage = "",
                        customNote = if (followUp) "" else it.customNote,
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        isAnalyzing = false,
                        isAskingFollowUp = false,
                        error = e.message
                            ?: app.getString(R.string.error_analyze_failed),
                        analyzeMessage = "",
                    )
                }
            }
        }
    }

    fun clearResult() {
        _ui.update {
            it.copy(
                result = null,
                menuText = "",
                customNote = "",
                isAskingFollowUp = false,
            )
        }
    }

    fun loadHistoryResult(entity: HistoryEntity) {
        val result = container.api.fromJson(entity.resultJson)
        _ui.update {
            it.copy(
                result = result,
                menuText = entity.menuPreview,
                customNote = "",
            )
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch { container.historyDao.delete(id) }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    val app = container.appContextRef.applicationContext as Application
                    return MenulyViewModel(app, container) as T
                }
            }
    }
}
