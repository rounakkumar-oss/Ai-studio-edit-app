package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey
    val id: String = DEFAULT_PREFS_ID,
    val isDarkTheme: Boolean = true,
    val defaultAspectRatio: String = "PORTRAIT_9_16",
    val defaultResolution: String = "1080p",
    val defaultFps: Int = 30,
    val proxyMediaPreview: Boolean = true,
    val hardwareAcceleration: Boolean = true,
    val autoBeatSync: Boolean = true,
    val autoCaptionsLanguage: String = "English",
    val lastEditedProjectId: String? = null
) {
    companion object {
        const val DEFAULT_PREFS_ID = "studio_user_preferences"
    }
}
