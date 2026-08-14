package com.menuly.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val mood: String,
    val menuPreview: String,
    val pickName: String,
    val pickPrice: String?,
    val score: Double,
    val why: String,
    val waiterNote: String,
    val resultJson: String,
)
