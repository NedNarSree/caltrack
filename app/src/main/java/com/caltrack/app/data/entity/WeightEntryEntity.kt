package com.caltrack.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weightKg: Float,
    val dateString: String, // format YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "" // e.g. "Today at 8:15 AM"
)
