package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream

data class ImportedMediaInfo(
    val uri: Uri,
    val title: String,
    val isVideo: Boolean,
    val isAudio: Boolean,
    val isImage: Boolean,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val rotation: Int
)

object MediaUtils {
    private const val TAG = "MediaUtils"

    fun queryMediaInfo(context: Context, uri: Uri): ImportedMediaInfo {
        var title = "Media"
        val contentResolver = context.contentResolver

        // Try to take persistable URI permission if possible
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
            // Some pickers or content providers do not support persistable permissions
        }

        // 1. Resolve Display Name
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    title = cursor.getString(nameIndex) ?: "Media"
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve display name: ${e.message}")
            title = uri.lastPathSegment ?: "Media"
        }

        val mimeType = try {
            contentResolver.getType(uri) ?: ""
        } catch (_: Exception) {
            ""
        }

        val isVideo = mimeType.startsWith("video/") || title.endsWith(".mp4", ignoreCase = true) ||
                title.endsWith(".mkv", ignoreCase = true) || title.endsWith(".mov", ignoreCase = true) ||
                title.endsWith(".webm", ignoreCase = true) || title.endsWith(".3gp", ignoreCase = true)

        val isAudio = mimeType.startsWith("audio/") || title.endsWith(".mp3", ignoreCase = true) ||
                title.endsWith(".wav", ignoreCase = true) || title.endsWith(".m4a", ignoreCase = true) ||
                title.endsWith(".aac", ignoreCase = true) || title.endsWith(".ogg", ignoreCase = true)

        val isImage = mimeType.startsWith("image/") || title.endsWith(".jpg", ignoreCase = true) ||
                title.endsWith(".jpeg", ignoreCase = true) || title.endsWith(".png", ignoreCase = true) ||
                title.endsWith(".webp", ignoreCase = true)

        var durationMs = if (isImage) 3000L else 5000L
        var width = if (isVideo || isImage) 1080 else 0
        var height = if (isVideo || isImage) 1920 else 0
        var rotation = 0

        if (isVideo || isAudio) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val durStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                if (!durStr.isNullOrBlank()) {
                    durationMs = durStr.toLongOrNull()?.coerceAtLeast(500L) ?: 5000L
                }

                if (isVideo) {
                    val wStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    val hStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    val rotStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    wStr?.toIntOrNull()?.let { width = it }
                    hStr?.toIntOrNull()?.let { height = it }
                    rotStr?.toIntOrNull()?.let { rotation = it }

                    // If rotated 90 or 270, swap dimensions
                    if (rotation == 90 || rotation == 270) {
                        val temp = width
                        width = height
                        height = temp
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "MediaMetadataRetriever failed for $uri: ${e.message}")
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {}
            }
        }

        return ImportedMediaInfo(
            uri = uri,
            title = title,
            isVideo = isVideo,
            isAudio = isAudio,
            isImage = isImage,
            durationMs = durationMs,
            width = width,
            height = height,
            rotation = rotation
        )
    }

    fun prepareMediaUriForEditing(context: Context, sourceUri: Uri): Uri {
        if (sourceUri.scheme == "file") return sourceUri
        return try {
            val extension = when {
                sourceUri.toString().contains(".mp4", ignoreCase = true) -> "mp4"
                sourceUri.toString().contains(".mov", ignoreCase = true) -> "mov"
                sourceUri.toString().contains(".mkv", ignoreCase = true) -> "mkv"
                sourceUri.toString().contains(".jpg", ignoreCase = true) -> "jpg"
                sourceUri.toString().contains(".jpeg", ignoreCase = true) -> "jpg"
                sourceUri.toString().contains(".png", ignoreCase = true) -> "png"
                sourceUri.toString().contains(".mp3", ignoreCase = true) -> "mp3"
                sourceUri.toString().contains(".wav", ignoreCase = true) -> "wav"
                else -> {
                    val mime = try { context.contentResolver.getType(sourceUri) } catch (_: Exception) { null }
                    when {
                        mime?.startsWith("video/") == true -> "mp4"
                        mime?.startsWith("image/") == true -> "jpg"
                        mime?.startsWith("audio/") == true -> "mp3"
                        else -> "dat"
                    }
                }
            }
            val mediaDir = File(context.filesDir, "media").apply { mkdirs() }
            val localFile = File(mediaDir, "media_${System.currentTimeMillis()}_${(1000..9999).random()}.$extension")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(localFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (localFile.exists() && localFile.length() > 0) {
                Uri.fromFile(localFile)
            } else {
                sourceUri
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to copy uri locally: ${e.message}")
            sourceUri
        }
    }

    fun extractFrameAtTime(context: Context, uri: Uri, timeMs: Long): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.getFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to extract frame at $timeMs ms: ${e.message}")
            null
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }
}
