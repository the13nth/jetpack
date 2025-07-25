package com.meticha.jetpackboilerplate.launcher.utils

import android.content.Context
import android.content.SharedPreferences
import com.meticha.jetpackboilerplate.launcher.data.models.RoutineType
import com.meticha.jetpackboilerplate.launcher.ui.screens.QuickActionData
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class QuickActionDataSerializable(
    val icon: String,
    val label: String,
    val packageName: String?
)

class PersistenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "launcher_preferences",
        Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
    
    fun saveAppsForRoutine(routineType: RoutineType, apps: List<QuickActionData>) {
        val key = "routine_apps_${routineType.name}"
        val serializableApps = apps.map { app ->
            QuickActionDataSerializable(app.icon, app.label, app.packageName)
        }
        
        try {
            val jsonString = json.encodeToString(serializableApps)
            sharedPreferences.edit().putString(key, jsonString).apply()
            android.util.Log.d("PersistenceManager", "Saved ${apps.size} apps for ${routineType.name}")
        } catch (e: Exception) {
            android.util.Log.e("PersistenceManager", "Failed to save apps for ${routineType.name}: ${e.message}")
        }
    }
    
    fun loadAppsForRoutine(routineType: RoutineType): List<QuickActionData> {
        val key = "routine_apps_${routineType.name}"
        val jsonString = sharedPreferences.getString(key, null)
        
        return if (jsonString != null) {
            try {
                val serializableApps = json.decodeFromString<List<QuickActionDataSerializable>>(jsonString)
                val apps = serializableApps.map { serializable ->
                    QuickActionData(serializable.icon, serializable.label, serializable.packageName)
                }
                android.util.Log.d("PersistenceManager", "Loaded ${apps.size} apps for ${routineType.name}")
                apps
            } catch (e: Exception) {
                android.util.Log.e("PersistenceManager", "Failed to load apps for ${routineType.name}: ${e.message}")
                emptyList()
            }
        } else {
            android.util.Log.d("PersistenceManager", "No saved apps found for ${routineType.name}")
            emptyList()
        }
    }
    
    fun clearAppsForRoutine(routineType: RoutineType) {
        val key = "routine_apps_${routineType.name}"
        sharedPreferences.edit().remove(key).apply()
        android.util.Log.d("PersistenceManager", "Cleared apps for ${routineType.name}")
    }
    
    fun clearAllApps() {
        val routineTypes = RoutineType.values()
        routineTypes.forEach { routineType ->
            clearAppsForRoutine(routineType)
        }
        android.util.Log.d("PersistenceManager", "Cleared all saved apps")
    }
} 