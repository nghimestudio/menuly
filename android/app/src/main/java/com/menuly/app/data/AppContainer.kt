package com.menuly.app.data

import android.content.Context
import androidx.room.Room
import com.menuly.app.data.api.MenulyApi
import com.menuly.app.data.db.HistoryDao
import com.menuly.app.data.db.MenulyDatabase
import com.menuly.app.data.locale.LocaleStore

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val db = Room.databaseBuilder(
        appContext,
        MenulyDatabase::class.java,
        "menuly.db"
    ).fallbackToDestructiveMigration().build()

    val historyDao: HistoryDao = db.historyDao()
    val api: MenulyApi = MenulyApi()
    val localeStore: LocaleStore = LocaleStore(appContext)
    val appContextRef: Context get() = appContext
}
