package com.menuly.app

import android.app.Application
import com.menuly.app.data.AppContainer
import com.menuly.app.data.locale.LocaleStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MenulyApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        // Apply saved locale before first Activity draws
        runBlocking {
            val tag = container.localeStore.languageTag.first()
            LocaleStore.applyLocale(tag)
        }
    }
}
