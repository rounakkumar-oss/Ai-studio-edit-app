package com.example.data.repository

import com.example.data.local.UserPreferencesDao
import com.example.data.local.UserPreferencesEntity
import com.example.data.model.AspectRatioType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserPreferences(
    val isDarkTheme: Boolean = true,
    val defaultAspectRatio: AspectRatioType = AspectRatioType.PORTRAIT_9_16,
    val defaultResolution: String = "1080p",
    val defaultFps: Int = 30,
    val proxyMediaPreview: Boolean = true,
    val hardwareAcceleration: Boolean = true,
    val autoBeatSync: Boolean = true,
    val autoCaptionsLanguage: String = "English",
    val lastEditedProjectId: String? = null
)

class UserPreferencesRepository(
    private val userPreferencesDao: UserPreferencesDao
) {

    val userPreferences: Flow<UserPreferences> = userPreferencesDao.getUserPreferences()
        .map { entity ->
            if (entity != null) {
                mapEntityToDomain(entity)
            } else {
                UserPreferences()
            }
        }

    suspend fun getPreferencesSync(): UserPreferences {
        val entity = userPreferencesDao.getUserPreferencesSync()
        return if (entity != null) mapEntityToDomain(entity) else UserPreferences()
    }

    suspend fun savePreferences(preferences: UserPreferences) {
        val entity = UserPreferencesEntity(
            isDarkTheme = preferences.isDarkTheme,
            defaultAspectRatio = preferences.defaultAspectRatio.name,
            defaultResolution = preferences.defaultResolution,
            defaultFps = preferences.defaultFps,
            proxyMediaPreview = preferences.proxyMediaPreview,
            hardwareAcceleration = preferences.hardwareAcceleration,
            autoBeatSync = preferences.autoBeatSync,
            autoCaptionsLanguage = preferences.autoCaptionsLanguage,
            lastEditedProjectId = preferences.lastEditedProjectId
        )
        userPreferencesDao.insertOrUpdatePreferences(entity)
    }

    suspend fun setProxyMediaPreview(enabled: Boolean) {
        userPreferencesDao.setProxyMediaPreview(enabled)
    }

    suspend fun setHardwareAcceleration(enabled: Boolean) {
        userPreferencesDao.setHardwareAcceleration(enabled)
    }

    suspend fun setExportDefaults(resolution: String, fps: Int) {
        userPreferencesDao.setExportDefaults(resolution, fps)
    }

    suspend fun setDefaultAspectRatio(aspectRatio: AspectRatioType) {
        userPreferencesDao.setDefaultAspectRatio(aspectRatio.name)
    }

    suspend fun setLastEditedProjectId(projectId: String?) {
        userPreferencesDao.setLastEditedProjectId(projectId)
    }

    private fun mapEntityToDomain(entity: UserPreferencesEntity): UserPreferences {
        val aspect = try {
            AspectRatioType.valueOf(entity.defaultAspectRatio)
        } catch (_: Exception) {
            AspectRatioType.PORTRAIT_9_16
        }
        return UserPreferences(
            isDarkTheme = entity.isDarkTheme,
            defaultAspectRatio = aspect,
            defaultResolution = entity.defaultResolution,
            defaultFps = entity.defaultFps,
            proxyMediaPreview = entity.proxyMediaPreview,
            hardwareAcceleration = entity.hardwareAcceleration,
            autoBeatSync = entity.autoBeatSync,
            autoCaptionsLanguage = entity.autoCaptionsLanguage,
            lastEditedProjectId = entity.lastEditedProjectId
        )
    }
}
