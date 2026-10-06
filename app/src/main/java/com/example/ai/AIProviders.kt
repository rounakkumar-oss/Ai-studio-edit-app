package com.example.ai

import com.example.data.model.CaptionItem
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow

sealed class AIResult<out T> {
    data class Success<out T>(val data: T, val modelName: String = "Gemini Flash") : AIResult<T>()
    data class Error(val message: String, val isNotConfigured: Boolean = false) : AIResult<Nothing>()
    object Loading : AIResult<Nothing>()
}

data class AIScriptScene(
    val sceneNumber: Int,
    val visualDescription: String,
    val voiceoverNarration: String,
    val cameraAngle: String,
    val durationSeconds: Int,
    val musicMood: String
)

data class AIScriptStoryboard(
    val title: String,
    val synopsis: String,
    val targetAspect: String,
    val scenes: List<AIScriptScene>,
    val suggestedBgm: String
)

data class AIVideoGenerationSpec(
    val prompt: String,
    val style: String, // Cinematic, 3D Anime, Cyberpunk, Hyper-realistic, Vintage Film
    val durationSeconds: Int = 4,
    val cameraMotion: String = "Pan Right",
    val referenceImageUri: String? = null
)

data class GeneratedVideoResult(
    val videoId: String,
    val title: String,
    val prompt: String,
    val durationMs: Long,
    val colorHex: Long,
    val thumbnailPlaceholder: String
)

data class GeneratedVoiceResult(
    val voiceId: String,
    val text: String,
    val voiceName: String,
    val language: String,
    val audioDurationMs: Long,
    val waveformPoints: List<Float>
)

data class GeneratedMusicResult(
    val musicId: String,
    val prompt: String,
    val genre: String,
    val mood: String,
    val bpm: Int,
    val durationMs: Long,
    val stemAvailable: Boolean
)

data class GeneratedImageResult(
    val imageId: String,
    val prompt: String,
    val style: String,
    val aspectRatio: String,
    val colorThemeHex: Long
)

// Provider interfaces per requirement 37
interface AIProvider {
    val name: String
    val isConfigured: Boolean
    val statusMessage: String
}

interface ScriptGenerationProvider : AIProvider {
    suspend fun generateScriptAndStoryboard(ideaPrompt: String, targetDurationSeconds: Int): AIResult<AIScriptStoryboard>
    suspend fun parseCommand(command: String, currentProject: Project): AIResult<CommandAction>
}

interface VideoGenerationProvider : AIProvider {
    suspend fun generateVideo(spec: AIVideoGenerationSpec): AIResult<GeneratedVideoResult>
}

interface ImageGenerationProvider : AIProvider {
    suspend fun generateImage(prompt: String, style: String, aspect: String): AIResult<GeneratedImageResult>
    suspend fun removeBackground(imageUri: String): AIResult<GeneratedImageResult>
}

interface VoiceGenerationProvider : AIProvider {
    suspend fun generateVoice(
        text: String,
        voiceName: String,
        language: String,
        speed: Float,
        pitch: Float
    ): AIResult<GeneratedVoiceResult>
}

interface MusicGenerationProvider : AIProvider {
    suspend fun generateMusic(
        prompt: String,
        genre: String,
        mood: String,
        bpm: Int,
        durationSeconds: Int
    ): AIResult<GeneratedMusicResult>
}

interface CaptionGenerationProvider : AIProvider {
    suspend fun autoGenerateCaptions(projectDurationMs: Long, audioContext: String): AIResult<List<CaptionItem>>
    suspend fun translateCaptions(captions: List<CaptionItem>, targetLanguage: String): AIResult<List<CaptionItem>>
}

sealed class CommandAction {
    data class ApplyFilter(val filterName: String) : CommandAction()
    data class AddCaptions(val language: String) : CommandAction()
    data class RemoveBackground(val trackId: String?) : CommandAction()
    data class TrimToReel(val durationMs: Long = 30000L) : CommandAction()
    data class BeatSync(val bpm: Int = 124) : CommandAction()
    data class AddIntro(val title: String) : CommandAction()
    data class Unknown(val rawCommand: String) : CommandAction()
}
