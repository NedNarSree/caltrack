package com.caltrack.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val heightCm: Float = 178f,
    val age: Int? = 29,
    val biologicalSex: String = "Male" // "Male", "Female", "Prefer not"
)
