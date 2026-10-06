package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ThumbnailExporter {
    private const val TAG = "ThumbnailExporter"

    suspend fun renderAndSaveThumbnail(
        context: Context,
        title: String,
        subtitle: String,
        badge: String,
        startColor: Long,
        endColor: Long,
        is16By9: Boolean,
        backgroundBitmap: Bitmap? = null
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val width = if (is16By9) 1920 else 1080
            val height = if (is16By9) 1080 else 1920

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 1. Draw Background
            if (backgroundBitmap != null) {
                val srcRect = Rect(0, 0, backgroundBitmap.width, backgroundBitmap.height)
                val dstRect = Rect(0, 0, width, height)
                canvas.drawBitmap(backgroundBitmap, srcRect, dstRect, null)

                // Dark vignette overlay
                val darkPaint = Paint().apply {
                    color = Color.argb(120, 0, 0, 0)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), darkPaint)
            } else {
                // Gradient Background
                val gradient = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    startColor.toInt(), endColor.toInt(),
                    Shader.TileMode.CLAMP
                )
                val bgPaint = Paint().apply { shader = gradient }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
            }

            // 2. Draw Badge (Pill button at top-left)
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#00E5FF")
            }
            val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = if (is16By9) 56f else 48f
                isFakeBoldText = true
            }

            val badgeBounds = Rect()
            badgeTextPaint.getTextBounds(badge, 0, badge.length, badgeBounds)
            val badgePaddingX = 40f
            val badgePaddingY = 24f
            val badgeLeft = 80f
            val badgeTop = 80f
            val badgeRight = badgeLeft + badgeBounds.width() + badgePaddingX * 2
            val badgeBottom = badgeTop + badgeBounds.height() + badgePaddingY * 2

            val badgeRect = RectF(badgeLeft, badgeTop, badgeRight, badgeBottom)
            canvas.drawRoundRect(badgeRect, 30f, 30f, badgePaint)
            canvas.drawText(
                badge,
                badgeLeft + badgePaddingX,
                badgeTop + badgePaddingY + badgeBounds.height(),
                badgeTextPaint
            )

            // 3. Draw Title Text with heavy drop shadow
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = if (is16By9) 110f else 92f
                isFakeBoldText = true
                setShadowLayer(25f, 6f, 6f, Color.BLACK)
            }

            val titleY = if (is16By9) height * 0.55f else height * 0.45f
            canvas.drawText(title, 80f, titleY, titlePaint)

            // 4. Draw Subtitle Text with yellow accent
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFD600")
                textSize = if (is16By9) 68f else 54f
                isFakeBoldText = true
                setShadowLayer(16f, 4f, 4f, Color.BLACK)
            }
            canvas.drawText(subtitle, 80f, titleY + (if (is16By9) 110f else 90f), subtitlePaint)

            // 5. Save to MediaStore (Pictures/AIStudio) or Cache
            val fileName = "AIStudio_Thumbnail_${System.currentTimeMillis()}.jpg"
            var savedUri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AIStudio")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                    savedUri = uri
                }
            }

            if (savedUri == null) {
                val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.cacheDir
                val file = File(picturesDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                savedUri = Uri.fromFile(file)
            }

            Result.success(savedUri)
        } catch (e: Exception) {
            Log.e(TAG, "Thumbnail export failed", e)
            Result.failure(e)
        }
    }
}
