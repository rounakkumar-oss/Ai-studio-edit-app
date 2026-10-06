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

    /**
     * Synthesizes an actual playable 16-bit PCM WAV audio file with rhythm, bass, and chords based on BPM and genre.
     */
    fun synthesizeMusicTrackToFile(
        context: android.content.Context,
        genre: String,
        mood: String,
        bpm: Int,
        durationSeconds: Int
    ): java.io.File {
        val sampleRate = 22050
        val totalSamples = sampleRate * durationSeconds.coerceIn(5, 60)
        val file = java.io.File(context.cacheDir, "music_${System.currentTimeMillis()}_${genre.take(4).lowercase()}.wav")

        val beatIntervalSamples = (sampleRate * 60.0 / bpm).toInt().coerceAtLeast(sampleRate / 4)
        val rootFreq = when {
            genre.contains("Lofi", ignoreCase = true) -> 220.0 // A3
            genre.contains("Synthwave", ignoreCase = true) -> 146.83 // D3
            genre.contains("Cinematic", ignoreCase = true) -> 110.0 // A2
            genre.contains("EDM", ignoreCase = true) -> 130.81 // C3
            else -> 164.81 // E3
        }

        val chordOffsets = listOf(1.0, 1.25, 1.5, 1.875) // Root, Maj/Min 3rd, 5th, 7th

        val byteBuffer = java.nio.ByteBuffer.allocate(44 + totalSamples * 2)
        byteBuffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)

        // WAV Header
        val subChunk2Size = totalSamples * 2
        val chunkSize = 36 + subChunk2Size

        byteBuffer.put("RIFF".toByteArray())
        byteBuffer.putInt(chunkSize)
        byteBuffer.put("WAVE".toByteArray())
        byteBuffer.put("fmt ".toByteArray())
        byteBuffer.putInt(16) // SubChunk1Size (16 for PCM)
        byteBuffer.putShort(1) // AudioFormat (1 for PCM)
        byteBuffer.putShort(1) // NumChannels (1 mono)
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(sampleRate * 2) // ByteRate
        byteBuffer.putShort(2) // BlockAlign
        byteBuffer.putShort(16) // BitsPerSample
        byteBuffer.put("data".toByteArray())
        byteBuffer.putInt(subChunk2Size)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val beatPos = i % beatIntervalSamples
            val isDownbeat = (i / beatIntervalSamples) % 4 == 0

            // 1. Kick drum at each beat
            var kick = 0.0
            if (beatPos < sampleRate * 0.15) {
                val kickT = beatPos.toDouble() / sampleRate
                val kickFreq = 120.0 * kotlin.math.exp(-kickT * 30.0)
                val kickEnv = kotlin.math.exp(-kickT * 20.0)
                kick = sin(2.0 * Math.PI * kickFreq * kickT) * kickEnv * 0.45
            }

            // 2. Chords / Lead synth
            var chord = 0.0
            chordOffsets.forEachIndexed { idx, mult ->
                val freq = rootFreq * mult
                chord += sin(2.0 * Math.PI * freq * t) * (0.12 / (idx + 1))
            }

            // 3. Bassline
            val bassFreq = rootFreq * 0.5
            val bass = sin(2.0 * Math.PI * bassFreq * t) * 0.22

            // 4. Hi-hat / Percussion (noise burst on offbeats)
            var hat = 0.0
            val hatPos = (beatPos + beatIntervalSamples / 2) % beatIntervalSamples
            if (hatPos < sampleRate * 0.04) {
                hat = ((Math.random() * 2.0) - 1.0) * (1.0 - (hatPos.toDouble() / (sampleRate * 0.04))) * 0.12
            }

            val mix = (kick + chord + bass + hat).coerceIn(-1.0, 1.0)
            val sampleShort = (mix * 32767.0).toInt().toShort()
            byteBuffer.putShort(sampleShort)
        }

        java.io.FileOutputStream(file).use { fos ->
            fos.write(byteBuffer.array())
        }

        return file
    }
}
