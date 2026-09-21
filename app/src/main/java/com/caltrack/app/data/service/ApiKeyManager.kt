package com.caltrack.app.data.service

import android.content.Context
import android.content.SharedPreferences

class ApiKeyManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getGeminiApiKey(): String {
        return prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
    }

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString(KEY_GEMINI_API_KEY, key.trim()).apply()
    }

    fun hasApiKey(): Boolean {
        return getGeminiApiKey().isNotBlank()
    }

    companion object {
        private const val PREFS_NAME = "caltrack_ai_prefs"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
    }
}
