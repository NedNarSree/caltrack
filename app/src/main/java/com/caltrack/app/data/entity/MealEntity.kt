package com.caltrack.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val dateString: String, // format YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "", // e.g. "8:20 AM"
    val imageUri: String? = null,
    val aiConfidence: Float? = null
)
