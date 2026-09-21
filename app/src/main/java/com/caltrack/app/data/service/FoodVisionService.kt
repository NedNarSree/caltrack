package com.caltrack.app.data.service

import android.graphics.Bitmap
import android.util.Base64
import com.caltrack.app.data.model.FoodAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class FoodVisionService(
    private val apiKeyManager: ApiKeyManager
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeFoodImage(
        bitmap: Bitmap,
        customApiKey: String? = null
    ): Result<FoodAnalysisResult> = withContext(Dispatchers.IO) {
        val key = (customApiKey ?: apiKeyManager.getGeminiApiKey()).trim()

        if (key.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API Key is missing. Please set your free Google AI Studio key in Profile / Settings.")
            )
        }

        try {
            // 1. Scale down bitmap to prevent massive payload size (max 1024x1024)
            val scaledBitmap = scaleBitmapDown(bitmap, 1024)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // 2. Build Gemini API JSON Payload
            val promptText = """
                You are a nutrition and food analysis expert. Analyze this food picture carefully.
                Identify the dish or food items present.
                Estimate realistic portion size in grams, total calories (kcal), and macronutrients:
                protein in grams, carbohydrates in grams, and fat in grams.
                Also provide a confidence score between 0.0 and 1.0, and a concise summary with main ingredients.
                
                Respond ONLY with a JSON object with this exact schema (no additional markdown or conversational text):
                {
                  "dishName": "Name of the dish",
                  "portionGrams": 250,
                  "calories": 450,
                  "proteinGrams": 32,
                  "carbsGrams": 40,
                  "fatGrams": 14,
                  "confidence": 0.94,
                  "summary": "Grilled chicken breast served with steamed brown rice and broccoli florets",
                  "ingredients": ["Chicken breast", "Brown rice", "Broccoli", "Olive oil"]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray()

                    // Text prompt part
                    partsArray.put(JSONObject().apply {
                        put("text", promptText)
                    })

                    // Inline image part
                    partsArray.put(JSONObject().apply {
                        val inlineData = JSONObject().apply {
                            put("mime_type", "image/jpeg")
                            put("data", base64Image)
                        }
                        put("inline_data", inlineData)
                    })

                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("response_mime_type", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", genConfig)
            }

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$key"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(responseBody)
                return@withContext Result.failure(Exception("Gemini Vision API error (${response.code}): $errorMsg"))
            }

            val parsedResult = parseGeminiResponse(responseBody)
            Result.success(parsedResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(jsonStr: String): String {
        return try {
            val json = JSONObject(jsonStr)
            val error = json.optJSONObject("error")
            error?.optString("message", jsonStr) ?: jsonStr
        } catch (_: Exception) {
            jsonStr
        }
    }

    private fun parseGeminiResponse(jsonStr: String): FoodAnalysisResult {
        val root = JSONObject(jsonStr)
        val candidates = root.getJSONArray("candidates")
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        val rawText = parts.getJSONObject(0).getString("text").trim()

        // Clean any accidental markdown wrap
        val cleanJson = if (rawText.startsWith("```")) {
            rawText.substringAfter("\n").substringBeforeLast("```").trim()
        } else {
            rawText
        }

        val json = JSONObject(cleanJson)

        val dishName = json.optString("dishName", "Analyzed Meal")
        val portionGrams = json.optInt("portionGrams", 200)
        val calories = json.optInt("calories", 400).coerceAtLeast(1)
        val proteinGrams = json.optInt("proteinGrams", 20).coerceAtLeast(0)
        val carbsGrams = json.optInt("carbsGrams", 35).coerceAtLeast(0)
        val fatGrams = json.optInt("fatGrams", 12).coerceAtLeast(0)
        val confidence = json.optDouble("confidence", 0.90).toFloat().coerceIn(0.1f, 1.0f)
        val summary = json.optString("summary", "Estimated nutritional values based on visual analysis.")

        val ingredientsList = mutableListOf<String>()
        val ingredientsArray = json.optJSONArray("ingredients")
        if (ingredientsArray != null) {
            for (i in 0 until ingredientsArray.length()) {
                ingredientsList.add(ingredientsArray.getString(i))
            }
        }

        return FoodAnalysisResult(
            dishName = dishName,
            portionGrams = portionGrams,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            confidence = confidence,
            summary = summary,
            ingredients = ingredientsList
        )
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (ratio > 1) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt()
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}
