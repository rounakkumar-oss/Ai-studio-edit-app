package com.example.audio

import kotlin.math.sin

data class AudioTrackConfig(
    val volume: Float = 1.0f,
    val fadeInMs: Long = 0L,
    val fadeOutMs: Long = 0L,
    val equalizerPreset: String = "Flat", // Flat, Bass Boost, Vocal Clarity, Electronic, Acoustic, Treble Boost
    val reverb: Float = 0f, // 0.0 to 1.0
    val echo: Float = 0f,   // 0.0 to 1.0
    val noiseReduction: Boolean = true,
    val vocalIsolation: Boolean = false,
    val normalizeAudio: Boolean = true
)

data class BeatMarker(
    val timestampMs: Long,
    val isDownbeat: Boolean
)

object AudioEngine {

    val equalizerPresets = listOf(
        "Flat",
        "Bass Boost Pro",
        "Vocal Clarity",
        "Electronic Club",
        "Acoustic Warmth",
        "Treble Boost",
        "Radio Mic"
    )

    val musicGenres = listOf(
        "Lofi Hip Hop",
        "Synthwave 80s",
        "Epic Cinematic",
        "Phonk Trap",
        "Acoustic Guitar",
        "Ambient Drone",
        "EDM Festival",
        "Indian Fusion",
        "Corporate Tech"
    )

    val musicMoods = listOf(
        "Energetic",
        "Chill / Relaxed",
        "Dramatic",
        "Inspiring",
        "Dark / Mysterious",
        "Joyful",
        "Nostalgic"
    )

    val soundEffectPacks = listOf(
        "Whoosh Whip Fast",
        "Cinematic Boom Drop",
        "Cyber Glitch Pop",
        "Camera Shutter Click",
        "Vinyl Scratch",
        "Riser Buildup",
        "Heartbeat Thud",
        "Laser Pew"
    )

    /**
     * Generates simulated beat markers for a given BPM and duration
     */
    fun calculateBeatGrid(bpm: Int, durationMs: Long): List<BeatMarker> {
        val intervalMs = (60000.0 / bpm).toLong()
        val beats = mutableListOf<BeatMarker>()
        var current = 0L
        var beatIndex = 0
        while (current < durationMs) {
            beats.add(BeatMarker(current, isDownbeat = (beatIndex % 4 == 0)))
            current += intervalMs
            beatIndex++
        }
        return beats
    }

    /**
     * Synthesizes audio waveform bars for visual rendering in the timeline
     */
    fun generateWaveformBars(clipDurationMs: Long, barCount: Int = 40): List<Float> {
        return List(barCount) { i ->
            val angle = i * 0.35f
            val base = (sin(angle.toDouble()).toFloat() * 0.4f + 0.6f)
            (base * (0.3f + 0.7f * ((i % 5) / 5f))).coerceIn(0.15f, 1.0f)
        }
    }
}
