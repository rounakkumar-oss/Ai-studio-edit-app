package com.example.video

import android.content.Context
import android.os.Environment
import com.example.data.model.Project
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

data class ExportOptions(
    val resolutionLabel: String = "1080p Full HD (1080x1920)",
    val width: Int = 1080,
    val height: Int = 1920,
    val fps: Int = 30,
    val bitrateMbps: Float = 12.0f,
    val format: String = "MP4",
    val hardwareAcceleration: Boolean = true
)

sealed class ExportState {
    object Idle : ExportState()
    data class Progress(val percent: Float, val currentFrame: Int, val totalFrames: Int, val etaSeconds: Int) : ExportState()
    data class Finished(val fileUri: String, val fileSizeMb: Float, val outputPath: String) : ExportState()
    data class Error(val message: String) : ExportState()
    object Cancelled : ExportState()
}

class ExportEngine(private val context: Context) {

    fun executeExport(project: Project, options: ExportOptions): Flow<ExportState> = flow {
        emit(ExportState.Progress(0f, 0, 100, 10))

        val durationSeconds = (project.durationMs / 1000f).coerceAtLeast(1f)
        val totalFrames = (durationSeconds * options.fps).toInt().coerceAtLeast(30)
        val step = (totalFrames / 50).coerceAtLeast(1)

        try {
            var currentFrame = 0
            while (currentFrame < totalFrames) {
                currentFrame += step
                if (currentFrame > totalFrames) currentFrame = totalFrames

                val progress = currentFrame.toFloat() / totalFrames.toFloat()
                val remainingFrames = totalFrames - currentFrame
                val eta = (remainingFrames / (options.fps * 1.5f)).toInt().coerceAtLeast(1)

                emit(ExportState.Progress(progress, currentFrame, totalFrames, eta))
                delay(60) // simulated hardware encoder frame pipeline
            }

            // Generate output file in standard movies directory
            val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "AIStudio_Exports")
            if (!outputDir.exists()) outputDir.mkdirs()

            val fileName = "AIStudio_${project.title.replace(" ", "_")}_${System.currentTimeMillis()}.${options.format.lowercase()}"
            val outputFile = File(outputDir, fileName)
            outputFile.writeText("AI Studio Export Container [${options.resolutionLabel}, ${options.fps}FPS, ${project.durationMs}ms]")

            val estimatedSizeMb = (durationSeconds * (options.bitrateMbps / 8.0f)).coerceAtLeast(2.4f)

            emit(
                ExportState.Finished(
                    fileUri = outputFile.toURI().toString(),
                    fileSizeMb = estimatedSizeMb,
                    outputPath = outputFile.absolutePath
                )
            )
        } catch (e: CancellationException) {
            emit(ExportState.Cancelled)
        } catch (e: Exception) {
            emit(ExportState.Error("Export failed: ${e.localizedMessage ?: "Unknown encoder error"}"))
        }
    }
}
