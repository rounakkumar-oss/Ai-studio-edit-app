package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val aspectRatio: String,
    val durationMs: Long,
    val fps: Int,
    val resolution: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val serializedData: String // JSON payload of tracks & clips
)
