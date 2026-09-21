package com.caltrack.app.data.model

data class FoodAnalysisResult(
    val dishName: String,
    val portionGrams: Int,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val confidence: Float,
    val summary: String,
    val ingredients: List<String> = emptyList()
)
