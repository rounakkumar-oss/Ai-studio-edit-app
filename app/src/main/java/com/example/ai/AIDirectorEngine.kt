package com.example.ai

import com.example.data.model.AspectRatioType
import com.example.data.model.ColorGradingSettings
import com.example.data.model.Project
import com.example.data.model.TextStyleModel
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import java.util.UUID

object AIDirectorEngine {

    enum class DirectorVibe(val label: String, val bpm: Int, val filterName: String, val transitionStyle: String) {
        CINEMATIC("Cinematic Dramatic", 96, "Teal & Orange", "Zoom In"),
        FAST_REEL("High Energy Reel", 132, "Cyberpunk", "Glitch Dissolve"),
        TRAVEL_VLOG("Travel Aesthetic", 110, "Warm Sunset", "Slide Left"),
        LOFI_CHILL("Lofi Nostalgia", 84, "Retro VHS", "Fade"),
        COMMERCIAL("Modern Commercial", 124, "Pastel Dream", "Camera Whip")
    }

    /**
     * Synthesizes an intelligent, multi-layer professional timeline project based on input parameters.
     */
    fun createAutoDirectedProject(
        conceptTitle: String,
        vibe: DirectorVibe,
        targetDurationSeconds: Int = 16,
        aspectRatio: AspectRatioType = AspectRatioType.PORTRAIT_9_16,
        rawClipNames: List<String> = emptyList()
    ): Project {
        val totalDurationMs = (targetDurationSeconds * 1000).toLong()
        val videoTrackId = UUID.randomUUID().toString()
        val bRollTrackId = UUID.randomUUID().toString()
        val audioTrackId = UUID.randomUUID().toString()
        val textTrackId = UUID.randomUUID().toString()
        val effectsTrackId = UUID.randomUUID().toString()

        val clipDurMs = when (vibe) {
            DirectorVibe.FAST_REEL -> 2000L
            DirectorVibe.CINEMATIC -> 4000L
            DirectorVibe.LOFI_CHILL -> 4000L
            else -> 3000L
        }

        val clipNames = if (rawClipNames.isNotEmpty()) rawClipNames else listOf(
            "Drone Establishing Shot.mp4",
            "Slow Motion Subject.mp4",
            "Action Detail Macro.mp4",
            "Scenic Landscape Panorama.mp4",
            "Hero Portrait Glance.mp4",
            "Sunset Golden Hour Finale.mp4"
        )

        val videoClips = mutableListOf<TimelineClip>()
        var currentMs = 0L
        var clipIndex = 0

        while (currentMs < totalDurationMs) {
            val dur = minOf(clipDurMs, totalDurationMs - currentMs)
            val name = clipNames[clipIndex % clipNames.size]
            val colorHex = when (clipIndex % 4) {
                0 -> 0xFF3D2C8D
                1 -> 0xFF916BBF
                2 -> 0xFF1B2A4A
                else -> 0xFF283618
            }

            videoClips.add(
                TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = videoTrackId,
                    title = name,
                    type = TrackType.VIDEO,
                    startMs = currentMs,
                    durationMs = dur,
                    sourceDurationMs = dur + 2000L,
                    speed = if (vibe == DirectorVibe.CINEMATIC) 0.8f else 1.0f,
                    transitionIn = if (clipIndex > 0) vibe.transitionStyle else "None",
                    previewColorHex = colorHex,
                    colorGrading = ColorGradingSettings(
                        lutFilter = vibe.filterName,
                        contrast = 1.15f,
                        saturation = 1.1f
                    )
                )
            )

            currentMs += dur
            clipIndex++
        }

        // B-Roll Overlay sticker / light leak
        val bRollClips = listOf(
            TimelineClip(
                id = UUID.randomUUID().toString(),
                trackId = bRollTrackId,
                title = "Cinematic Light Leak.mov",
                type = TrackType.OVERLAY,
                startMs = 0L,
                durationMs = 3500L,
                opacity = 0.65f,
                previewColorHex = 0xFFFFB300
            )
        )

        // Audio Track with beat sync
        val audioClips = listOf(
            TimelineClip(
                id = UUID.randomUUID().toString(),
                trackId = audioTrackId,
                title = "${vibe.label} Beat (${vibe.bpm} BPM).mp3",
                type = TrackType.AUDIO,
                startMs = 0L,
                durationMs = totalDurationMs,
                volume = 0.9f,
                previewColorHex = 0xFF00ADB5
            )
        )

        // Text & Captions with auto pop animations
        val textClips = listOf(
            TimelineClip(
                id = UUID.randomUUID().toString(),
                trackId = textTrackId,
                title = "Main Title Card",
                type = TrackType.TEXT,
                startMs = 400L,
                durationMs = 3200L,
                previewColorHex = 0xFFFF2E93,
                textStyle = TextStyleModel(
                    text = conceptTitle.uppercase(),
                    fontSizeSp = 28f,
                    animation = "Kinetic Wave"
                )
            ),
            TimelineClip(
                id = UUID.randomUUID().toString(),
                trackId = textTrackId,
                title = "Director Stamp",
                type = TrackType.TEXT,
                startMs = 3800L,
                durationMs = 3000L,
                previewColorHex = 0xFFFF5370,
                textStyle = TextStyleModel(
                    text = "AI DIRECTOR EDIT",
                    fontSizeSp = 20f,
                    animation = "Pop Bounce"
                )
            )
        )

        val tracks = listOf(
            TimelineTrack(id = videoTrackId, name = "A-Roll Video", type = TrackType.VIDEO, clips = videoClips),
            TimelineTrack(id = bRollTrackId, name = "B-Roll Overlay", type = TrackType.OVERLAY, clips = bRollClips),
            TimelineTrack(id = audioTrackId, name = "Music Track", type = TrackType.AUDIO, clips = audioClips),
            TimelineTrack(id = textTrackId, name = "AI Text & Captions", type = TrackType.TEXT, clips = textClips)
        )

        return Project(
            id = UUID.randomUUID().toString(),
            title = conceptTitle,
            aspectRatio = aspectRatio,
            durationMs = totalDurationMs,
            tracks = tracks
        )
    }
}
