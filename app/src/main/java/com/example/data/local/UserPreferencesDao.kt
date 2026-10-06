package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {

    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    fun getUserPreferences(id: String = UserPreferencesEntity.DEFAULT_PREFS_ID): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = :id LIMIT 1")
    suspend fun getUserPreferencesSync(id: String = UserPreferencesEntity.DEFAULT_PREFS_ID): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: UserPreferencesEntity)

    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)

    @Query("UPDATE user_preferences SET proxyMediaPreview = :enabled WHERE id = :id")
    suspend fun setProxyMediaPreview(enabled: Boolean, id: String = UserPreferencesEntity.DEFAULT_PREFS_ID)

    @Query("UPDATE user_preferences SET hardwareAcceleration = :enabled WHERE id = :id")
    suspend fun setHardwareAcceleration(enabled: Boolean, id: String = UserPreferencesEntity.DEFAULT_PREFS_ID)

    @Query("UPDATE user_preferences SET defaultResolution = :resolution, defaultFps = :fps WHERE id = :id")
    suspend fun setExportDefaults(resolution: String, fps: Int, id: String = UserPreferencesEntity.DEFAULT_PREFS_ID)

    @Query("UPDATE user_preferences SET defaultAspectRatio = :aspect WHERE id = :id")
    suspend fun setDefaultAspectRatio(aspect: String, id: String = UserPreferencesEntity.DEFAULT_PREFS_ID)

    @Query("UPDATE user_preferences SET lastEditedProjectId = :projectId WHERE id = :id")
    suspend fun setLastEditedProjectId(projectId: String?, id: String = UserPreferencesEntity.DEFAULT_PREFS_ID)
}
