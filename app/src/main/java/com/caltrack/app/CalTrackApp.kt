package com.caltrack.app

import android.app.Application
import com.caltrack.app.data.CalTrackDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CalTrackApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy {
        CalTrackDatabase.getDatabase(this, applicationScope)
    }

    val apiKeyManager by lazy {
        com.caltrack.app.data.service.ApiKeyManager(this)
    }

    val foodVisionService by lazy {
        com.caltrack.app.data.service.FoodVisionService(apiKeyManager)
    }
}
