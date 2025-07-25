package com.example.rotiie.launcher.utils

object Constants {
    // Database
    const val DATABASE_NAME = "launcher_database"
    const val DATABASE_VERSION = 1
    
    // Default routine times (24-hour format)
    const val DEFAULT_MORNING_START = "06:00"
    const val DEFAULT_MORNING_END = "12:00"
    const val DEFAULT_AFTERNOON_START = "12:00"
    const val DEFAULT_AFTERNOON_END = "18:00"
    const val DEFAULT_EVENING_START = "18:00"
    const val DEFAULT_EVENING_END = "23:00"
    const val DEFAULT_WEEKEND_START = "08:00"
    const val DEFAULT_WEEKEND_END = "23:00"
    
    // Behavior tracking
    const val MAX_USER_ACTIONS_STORED = 10000
    const val USER_ACTION_CLEANUP_DAYS = 30
    const val MIN_PATTERN_FREQUENCY = 3
    const val MIN_SUCCESS_RATE_THRESHOLD = 0.6f
    
    // UI Configuration
    const val MAX_PREDICTED_ACTIONS = 5
    const val MIN_PREDICTION_CONFIDENCE = 0.3f
    const val UI_UPDATE_DEBOUNCE_MS = 500L
    
    // Preferences
    const val PREFS_NAME = "launcher_preferences"
    const val PREF_CURRENT_ROUTINE = "current_routine"
    const val PREF_FIRST_LAUNCH = "first_launch"
    const val PREF_LEARNING_ENABLED = "learning_enabled"
}