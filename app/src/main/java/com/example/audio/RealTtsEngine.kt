package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID

data class RealTtsResult(
    val audioFile: File,
    val audioUri: Uri,
    val durationMs: Long,
    val language: String,
    val voiceName: String,
    val text: String
)

class RealTtsEngine(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val initDeferred = CompletableDeferred<Boolean>()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale.ENGLISH
                initDeferred.complete(true)
            } else {
                Log.e("RealTtsEngine", "TTS initialization failed with code: $status")
                initDeferred.complete(false)
            }
        }
    }

    suspend fun awaitInitialization(): Boolean {
        return if (isInitialized) true else initDeferred.await()
    }

    suspend fun synthesizeSpeechToFile(
        text: String,
        voiceName: String,
        language: String,
        speed: Float,
        pitch: Float
    ): Result<RealTtsResult> = withContext(Dispatchers.IO) {
        val ready = awaitInitialization()
        if (!ready || tts == null) {
            return@withContext Result.failure(Exception("Android TextToSpeech engine could not be initialized on device"))
        }

        try {
            val engine = tts!!
            // Match requested language
            val locale = when (language.lowercase()) {
                "hindi", "hi", "hinglish" -> Locale("hi", "IN")
                "spanish", "es" -> Locale("es", "ES")
                "french", "fr" -> Locale.FRENCH
                "german", "de" -> Locale.GERMAN
                "japanese", "ja" -> Locale.JAPANESE
                "bengali", "bn" -> Locale("bn", "IN")
                "marathi", "mr" -> Locale("mr", "IN")
                "tamil", "ta" -> Locale("ta", "IN")
                "telugu", "te" -> Locale("te", "IN")
                else -> Locale.US
            }

            // Set language; fallback to default if not available
            val langResult = engine.setLanguage(locale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                engine.language = Locale.US
            }

            engine.setSpeechRate(speed.coerceIn(0.25f, 3.0f))
            engine.setPitch(pitch.coerceIn(0.25f, 2.5f))

            val outputFile = File(context.cacheDir, "tts_${UUID.randomUUID().toString().take(8)}.wav")
            val utteranceId = "utterance_${System.currentTimeMillis()}"
            val completion = CompletableDeferred<Boolean>()

            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {}
                override fun onDone(id: String?) {
                    if (id == utteranceId) completion.complete(true)
                }
                override fun onError(id: String?) {
                    if (id == utteranceId) completion.complete(false)
                }
                @Deprecated("Deprecated in Java")
                override fun onError(id: String?, errorCode: Int) {
                    if (id == utteranceId) completion.complete(false)
                }
            })

            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            val synthResult = engine.synthesizeToFile(text, params, outputFile, utteranceId)

            if (synthResult != TextToSpeech.SUCCESS) {
                return@withContext Result.failure(Exception("TTS synthesizeToFile returned error code: $synthResult"))
            }

            // Wait for completion (timeout 15 seconds)
            kotlinx.coroutines.withTimeoutOrNull(15000L) {
                completion.await()
            }

            // Calculate duration of synthesized audio
            var durationMs = 3000L
            if (outputFile.exists() && outputFile.length() > 0) {
                try {
                    val mp = MediaPlayer()
                    mp.setDataSource(outputFile.absolutePath)
                    mp.prepare()
                    durationMs = mp.duration.toLong().coerceAtLeast(1000L)
                    mp.release()
                } catch (_: Exception) {
                    val wordCount = text.split("\\s+".toRegex()).size
                    durationMs = (wordCount * (60000L / (130 * speed).toInt())).coerceAtLeast(1500L)
                }
            } else {
                return@withContext Result.failure(Exception("TTS output audio file was not written"))
            }

            val ttsResult = RealTtsResult(
                audioFile = outputFile,
                audioUri = Uri.fromFile(outputFile),
                durationMs = durationMs,
                language = language,
                voiceName = voiceName,
                text = text
            )

            Result.success(ttsResult)
        } catch (e: Exception) {
            Log.e("RealTtsEngine", "Synthesis failed", e)
            Result.failure(e)
        }
    }

    fun speakPreview(text: String, language: String, speed: Float, pitch: Float) {
        if (!isInitialized || tts == null) return
        val engine = tts!!
        val locale = when (language.lowercase()) {
            "hindi", "hi", "hinglish" -> Locale("hi", "IN")
            else -> Locale.US
        }
        engine.setLanguage(locale)
        engine.setSpeechRate(speed)
        engine.setPitch(pitch)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "preview_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
