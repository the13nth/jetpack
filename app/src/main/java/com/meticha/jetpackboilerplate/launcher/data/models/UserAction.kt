package com.meticha.jetpackboilerplate.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserAction(
    val id: String,
    val type: ActionType,
    val timestamp: Long = System.currentTimeMillis(),
    val routineId: String,
    val appPackageName: String? = null,
    val contextData: String? = null, // JSON string for additional context
    val outcome: ActionOutcome? = null,
    val duration: Long? = null // Duration in milliseconds
)

@Serializable
enum class ActionType {
    APP_LAUNCH,
    ROUTINE_SWITCH,
    INTENTION_SET,
    DOCUMENT_UPLOAD,
    UI_INTERACTION,
    NUDGE_RESPONSE,
    CUSTOM
}

@Serializable
enum class ActionOutcome {
    SUCCESS,
    FAILURE,
    CANCELLED,
    TIMEOUT,
    INTERRUPTED
}