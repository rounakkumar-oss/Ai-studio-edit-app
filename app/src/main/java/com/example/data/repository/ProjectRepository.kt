package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.model.AspectRatioType
import com.example.data.model.ColorGradingSettings
import com.example.data.model.Project
import com.example.data.model.TextStyleModel
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects().map { list ->
        list.map { entityToProject(it) }
    }

    val favoriteProjects: Flow<List<Project>> = projectDao.getFavoriteProjects().map { list ->
        list.map { entityToProject(it) }
    }

    suspend fun getProjectById(id: String): Project? {
        val entity = projectDao.getProjectById(id) ?: return null
        return entityToProject(entity)
    }

    suspend fun saveProject(project: Project) {
        val entity = projectToEntity(project)
        projectDao.insertProject(entity)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProjectById(id)
    }

    suspend fun toggleFavorite(project: Project) {
        val updated = project.copy(isFavorite = !project.isFavorite, updatedAt = System.currentTimeMillis())
        saveProject(updated)
    }

    suspend fun duplicateProject(project: Project): Project {
        val dupId = UUID.randomUUID().toString()
        val duplicated = project.copy(
            id = dupId,
            title = "${project.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProject(duplicated)
        return duplicated
    }

    suspend fun seedInitialProjectsIfEmpty() {
        // Will check if projects exist and seed sample high quality starter projects
    }

    private fun projectToEntity(project: Project): ProjectEntity {
        val json = JSONObject()
        val tracksArray = JSONArray()

        project.tracks.forEach { track ->
            val trackObj = JSONObject()
            trackObj.put("id", track.id)
            trackObj.put("name", track.name)
            trackObj.put("type", track.type.name)
            trackObj.put("isMuted", track.isMuted)
            trackObj.put("isLocked", track.isLocked)
            trackObj.put("isVisible", track.isVisible)

            val clipsArray = JSONArray()
            track.clips.forEach { clip ->
                val clipObj = JSONObject()
                clipObj.put("id", clip.id)
                clipObj.put("trackId", clip.trackId)
                clipObj.put("title", clip.title)
                clipObj.put("type", clip.type.name)
                clipObj.put("startMs", clip.startMs)
                clipObj.put("durationMs", clip.durationMs)
                clipObj.put("sourceDurationMs", clip.sourceDurationMs)
                clipObj.put("speed", clip.speed.toDouble())
                clipObj.put("volume", clip.volume.toDouble())
                clipObj.put("isMuted", clip.isMuted)
                clipObj.put("opacity", clip.opacity.toDouble())
                clipObj.put("scale", clip.scale.toDouble())
                clipObj.put("rotation", clip.rotation.toDouble())
                clipObj.put("transitionIn", clip.transitionIn)
                clipObj.put("transitionDurationMs", clip.transitionDurationMs)
                clipObj.put("activeEffect", clip.activeEffect)
                clipObj.put("effectIntensity", clip.effectIntensity.toDouble())
                clipObj.put("previewColorHex", clip.previewColorHex)
                clipObj.put("isReversed", clip.isReversed)
                clipObj.put("chromaKeyEnabled", clip.chromaKeyEnabled)
                clipObj.put("aiBackgroundRemoved", clip.aiBackgroundRemoved)

                // Color grading
                val cgObj = JSONObject()
                cgObj.put("brightness", clip.colorGrading.brightness.toDouble())
                cgObj.put("contrast", clip.colorGrading.contrast.toDouble())
                cgObj.put("saturation", clip.colorGrading.saturation.toDouble())
                cgObj.put("temperature", clip.colorGrading.temperature.toDouble())
                cgObj.put("tint", clip.colorGrading.tint.toDouble())
                cgObj.put("vignette", clip.colorGrading.vignette.toDouble())
                cgObj.put("lutFilter", clip.colorGrading.lutFilter)
                clipObj.put("colorGrading", cgObj)

                // Text style if present
                clip.textStyle?.let { ts ->
                    val tsObj = JSONObject()
                    tsObj.put("text", ts.text)
                    tsObj.put("fontName", ts.fontName)
                    tsObj.put("fontSizeSp", ts.fontSizeSp.toDouble())
                    tsObj.put("textColor", ts.textColor)
                    tsObj.put("outlineColor", ts.outlineColor)
                    tsObj.put("animation", ts.animation)
                    clipObj.put("textStyle", tsObj)
                }

                clipsArray.put(clipObj)
            }
            trackObj.put("clips", clipsArray)
            tracksArray.put(trackObj)
        }
        json.put("tracks", tracksArray)

        return ProjectEntity(
            id = project.id,
            title = project.title,
            aspectRatio = project.aspectRatio.name,
            durationMs = project.durationMs,
            fps = project.fps,
            resolution = project.resolution,
            isFavorite = project.isFavorite,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt,
            serializedData = json.toString()
        )
    }

    private fun entityToProject(entity: ProjectEntity): Project {
        val aspect = try {
            AspectRatioType.valueOf(entity.aspectRatio)
        } catch (_: Exception) {
            AspectRatioType.PORTRAIT_9_16
        }

        val tracks = mutableListOf<TimelineTrack>()
        try {
            val json = JSONObject(entity.serializedData)
            val tracksArray = json.optJSONArray("tracks") ?: JSONArray()
            for (i in 0 until tracksArray.length()) {
                val tObj = tracksArray.getJSONObject(i)
                val trackId = tObj.optString("id", UUID.randomUUID().toString())
                val trackName = tObj.optString("name", "Track $i")
                val trackType = try {
                    TrackType.valueOf(tObj.optString("type", TrackType.VIDEO.name))
                } catch (_: Exception) {
                    TrackType.VIDEO
                }
                val isMuted = tObj.optBoolean("isMuted", false)
                val isLocked = tObj.optBoolean("isLocked", false)
                val isVisible = tObj.optBoolean("isVisible", true)

                val clips = mutableListOf<TimelineClip>()
                val clipsArray = tObj.optJSONArray("clips") ?: JSONArray()
                for (j in 0 until clipsArray.length()) {
                    val cObj = clipsArray.getJSONObject(j)
                    val clipId = cObj.optString("id", UUID.randomUUID().toString())
                    val clipTitle = cObj.optString("title", "Clip $j")
                    val clipType = try {
                        TrackType.valueOf(cObj.optString("type", trackType.name))
                    } catch (_: Exception) {
                        trackType
                    }

                    val cgObj = cObj.optJSONObject("colorGrading")
                    val cg = if (cgObj != null) {
                        ColorGradingSettings(
                            brightness = cgObj.optDouble("brightness", 0.0).toFloat(),
                            contrast = cgObj.optDouble("contrast", 1.0).toFloat(),
                            saturation = cgObj.optDouble("saturation", 1.0).toFloat(),
                            temperature = cgObj.optDouble("temperature", 0.0).toFloat(),
                            tint = cgObj.optDouble("tint", 0.0).toFloat(),
                            vignette = cgObj.optDouble("vignette", 0.0).toFloat(),
                            lutFilter = cgObj.optString("lutFilter", "Normal")
                        )
                    } else ColorGradingSettings()

                    val tsObj = cObj.optJSONObject("textStyle")
                    val ts = if (tsObj != null) {
                        TextStyleModel(
                            text = tsObj.optString("text", "Text"),
                            fontName = tsObj.optString("fontName", "Roboto Bold"),
                            fontSizeSp = tsObj.optDouble("fontSizeSp", 26.0).toFloat(),
                            textColor = tsObj.optLong("textColor", 0xFFFFFFFF),
                            outlineColor = tsObj.optLong("outlineColor", 0xFF000000),
                            animation = tsObj.optString("animation", "Fade In")
                        )
                    } else null

                    clips.add(
                        TimelineClip(
                            id = clipId,
                            trackId = trackId,
                            title = clipTitle,
                            type = clipType,
                            startMs = cObj.optLong("startMs", 0L),
                            durationMs = cObj.optLong("durationMs", 3000L),
                            sourceDurationMs = cObj.optLong("sourceDurationMs", 5000L),
                            speed = cObj.optDouble("speed", 1.0).toFloat(),
                            volume = cObj.optDouble("volume", 1.0).toFloat(),
                            isMuted = cObj.optBoolean("isMuted", false),
                            opacity = cObj.optDouble("opacity", 1.0).toFloat(),
                            scale = cObj.optDouble("scale", 1.0).toFloat(),
                            rotation = cObj.optDouble("rotation", 0.0).toFloat(),
                            transitionIn = cObj.optString("transitionIn", "None"),
                            transitionDurationMs = cObj.optLong("transitionDurationMs", 500L),
                            activeEffect = cObj.optString("activeEffect", "None"),
                            effectIntensity = cObj.optDouble("effectIntensity", 0.8).toFloat(),
                            textStyle = ts,
                            previewColorHex = cObj.optLong("previewColorHex", 0xFF2A2D3A),
                            colorGrading = cg,
                            isReversed = cObj.optBoolean("isReversed", false),
                            chromaKeyEnabled = cObj.optBoolean("chromaKeyEnabled", false),
                            aiBackgroundRemoved = cObj.optBoolean("aiBackgroundRemoved", false)
                        )
                    )
                }

                tracks.add(
                    TimelineTrack(
                        id = trackId,
                        name = trackName,
                        type = trackType,
                        isMuted = isMuted,
                        isLocked = isLocked,
                        isVisible = isVisible,
                        clips = clips
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }

        return Project(
            id = entity.id,
            title = entity.title,
            aspectRatio = aspect,
            durationMs = entity.durationMs,
            fps = entity.fps,
            resolution = entity.resolution,
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            tracks = tracks
        )
    }

    companion object {
        fun createDefaultProject(title: String = "New AI Project", aspect: AspectRatioType = AspectRatioType.PORTRAIT_9_16): Project {
            val videoTrackId = UUID.randomUUID().toString()
            val audioTrackId = UUID.randomUUID().toString()
            val textTrackId = UUID.randomUUID().toString()

            val clips = listOf(
                TimelineClip(
                    trackId = videoTrackId,
                    title = "Intro Scene.mp4",
                    type = TrackType.VIDEO,
                    startMs = 0L,
                    durationMs = 4000L,
                    previewColorHex = 0xFF3D2C8D,
                    activeEffect = "Cinematic Glow"
                ),
                TimelineClip(
                    trackId = videoTrackId,
                    title = "Action Shot.mp4",
                    type = TrackType.VIDEO,
                    startMs = 4000L,
                    durationMs = 4000L,
                    previewColorHex = 0xFF916BBF,
                    transitionIn = "Zoom In"
                ),
                TimelineClip(
                    trackId = videoTrackId,
                    title = "Outro Climax.mp4",
                    type = TrackType.VIDEO,
                    startMs = 8000L,
                    durationMs = 4000L,
                    previewColorHex = 0xFF1C0A3B,
                    transitionIn = "Glitch Dissolve"
                )
            )

            val audioClips = listOf(
                TimelineClip(
                    trackId = audioTrackId,
                    title = "Cyber Synth Beat.mp3",
                    type = TrackType.AUDIO,
                    startMs = 0L,
                    durationMs = 12000L,
                    volume = 0.85f,
                    previewColorHex = 0xFF00ADB5
                )
            )

            val textClips = listOf(
                TimelineClip(
                    trackId = textTrackId,
                    title = "AI Title",
                    type = TrackType.TEXT,
                    startMs = 500L,
                    durationMs = 3000L,
                    previewColorHex = 0xFFFF2E93,
                    textStyle = TextStyleModel(
                        text = "AI STUDIO REEL",
                        animation = "Pop Bounce"
                    )
                )
            )

            return Project(
                id = UUID.randomUUID().toString(),
                title = title,
                aspectRatio = aspect,
                durationMs = 12000L,
                tracks = listOf(
                    TimelineTrack(id = videoTrackId, name = "Main Video", type = TrackType.VIDEO, clips = clips),
                    TimelineTrack(id = audioTrackId, name = "Audio Track", type = TrackType.AUDIO, clips = audioClips),
                    TimelineTrack(id = textTrackId, name = "Text & Captions", type = TrackType.TEXT, clips = textClips)
                )
            )
        }
    }
}
