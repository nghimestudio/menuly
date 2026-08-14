package com.menuly.app.data.locale

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.menuly.app.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.localeDataStore by preferencesDataStore("menuly_locale")

data class AppLanguage(
    /** Empty string = follow system */
    val tag: String,
    val labelRes: Int,
)

object AppLanguages {
    val all = listOf(
        AppLanguage("", R.string.lang_system),
        AppLanguage("en", R.string.lang_english),
        AppLanguage("vi", R.string.lang_vietnamese),
        AppLanguage("zh", R.string.lang_chinese),
        AppLanguage("ja", R.string.lang_japanese),
        AppLanguage("ko", R.string.lang_korean),
    )

    fun apiLanguageCode(): String {
        val app = AppCompatDelegate.getApplicationLocales()
        val tag = if (!app.isEmpty) app[0]?.language else null
        val lang = tag ?: Locale.getDefault().language
        return when (lang) {
            "vi", "zh", "ja", "ko", "en" -> lang
            else -> "en"
        }
    }
}

class LocaleStore(private val context: Context) {
    private val keyTag = stringPreferencesKey("language_tag")
    private val keyChosen = booleanPreferencesKey("language_chosen")

    val languageTag: Flow<String> = context.localeDataStore.data.map { it[keyTag] ?: "" }
    val hasChosenLanguage: Flow<Boolean> =
        context.localeDataStore.data.map { it[keyChosen] == true }

    suspend fun setLanguage(tag: String, markChosen: Boolean = true) {
        context.localeDataStore.edit { prefs ->
            prefs[keyTag] = tag
            if (markChosen) prefs[keyChosen] = true
        }
        applyLocale(tag)
    }

    companion object {
        fun applyLocale(tag: String) {
            val locales = if (tag.isBlank()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(tag)
            }
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }
}
