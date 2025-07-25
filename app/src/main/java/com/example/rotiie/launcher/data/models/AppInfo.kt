package com.example.rotiie.launcher.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AppInfo(
    val packageName: String,
    val appName: String,
    val className: String? = null,
    val icon: String? = null, // Base64 encoded or resource path
    val category: String? = null,
    val lastUsed: Long? = null,
    val usageCount: Int = 0,
    val isSystemApp: Boolean = false
)