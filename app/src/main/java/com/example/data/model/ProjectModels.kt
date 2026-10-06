package com.example.data.model

import java.util.UUID

enum class TrackType {
    VIDEO,
    OVERLAY,
    AUDIO,
    TEXT,
    EFFECT
}

enum class AspectRatioType(val label: String, val ratioWidth: Float, val ratioHeight: Float, val iconDescription: String) {
    PORTRAIT_9_16("9:16 Reels/TikTok", 9f, 16f, "Vertical Video (9:16)"),
    LANDSCAPE_16_9("16:9 YouTube", 16f, 9f, "Horizontal Video (16:9)"),
    SQUARE_1_1("1:1 Instagram Post", 1f, 1f, "Square (1:1)"),
    PORTRAIT_4_5("4:5 Feed", 4f, 5f, "Feed Post (4:5)"),
    STANDARD_4_3("4:3 Classic", 4f, 3f, "Standard (4:3)")
}

data class ColorGradingSettings(
    val brightness: Float = 0f,      // -1.0 to 1.0
    val contrast: Float = 1.0f,      // 0.0 to 2.0
    val saturation: Float = 1.0f,    // 0.0 to 2.0
    val temperature: Float = 0f,     // -1.0 to 1.0 (warm/cool)
    val tint: Float = 0f,            // -1.0 to 1.0 (green/magenta)
    val vignette: Float = 0f,        // 0.0 to 1.0
    val sharpen: Float = 0f,         // 0.0 to 1.0
    val lutFilter: String = "Normal" // Normal, Teal & Orange, Cyberpunk, Film Noir, Warm Sunset, Retro VHS, Pastel Dream
)

data class TextStyleModel(
    val text: String = "AI Studio",
    val fontName: String = "Roboto Bold",
    val fontSizeSp: Float = 26f,
    val textColor: Long = 0xFFFFFFFF,
    val outlineColor: Long = 0xFF000000,
    val outlineWidth: Float = 2f,
    val glowColor: Long = 0x887C4DFF,
    val hasShadow: Boolean = true,
    val animation: String = "Fade In", // Fade In, Typewriter, Pop Bounce, Slide Up, Kinetic Wave
    val isWordHighlightKaraoke: Boolean = false
)

data class TimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val trackId: String,
    val title: String,
    val type: TrackType,
    val startMs: Long = 0L,
    val durationMs: Long = 3000L,
    val sourceDurationMs: Long = 5000L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val opacity: Float = 1.0f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val colorGrading: ColorGradingSettings = ColorGradingSettings(),
    val transitionIn: String = "None",
    val transitionDurationMs: Long = 500L,
    val activeEffect: String = "None",
    val effectIntensity: Float = 0.8f,
    val textStyle: TextStyleModel? = null,
    val mediaUri: String? = null,
    val previewColorHex: Long = 0xFF2A2D3A,
    val isReversed: Boolean = false,
    val chromaKeyEnabled: Boolean = false,
    val aiBackgroundRemoved: Boolean = false
)

data class TimelineTrack(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TrackType,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isVisible: Boolean = true,
    val clips: List<TimelineClip> = emptyList()
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Untitled Project",
    val aspectRatio: AspectRatioType = AspectRatioType.PORTRAIT_9_16,
    val durationMs: Long = 12000L,
    val fps: Int = 30,
    val resolution: String = "1080p",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tracks: List<TimelineTrack> = emptyList()
)

data class CaptionItem(
    val id: String = UUID.randomUUID().toString(),
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val translatedText: String? = null,
    val confidence: Float = 0.95f
)

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val durationSeconds: Int,
    val aspect: AspectRatioType,
    val description: String,
    val musicGenre: String,
    val tags: List<String>,
    val colorGradient: Pair<Long, Long>
)
