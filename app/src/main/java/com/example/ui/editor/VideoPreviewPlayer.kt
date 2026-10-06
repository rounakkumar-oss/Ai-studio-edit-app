package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatioType
import com.example.data.model.Project
import com.example.data.model.TrackType
import kotlin.math.sin

@Composable
fun VideoPreviewPlayer(
    project: Project?,
    playheadMs: Long,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onToggleMute: () -> Unit,
    isMuted: Boolean,
    modifier: Modifier = Modifier
) {
    val aspect = project?.aspectRatio ?: AspectRatioType.PORTRAIT_9_16
    val ratio = aspect.ratioWidth / aspect.ratioHeight

    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Find currently active clips at playhead
    val activeVideoClips = remember(project, playheadMs) {
        project?.tracks
            ?.filter { it.type == TrackType.VIDEO || it.type == TrackType.OVERLAY }
            ?.flatMap { track ->
                track.clips.filter { playheadMs >= it.startMs && playheadMs < (it.startMs + it.durationMs) }
            } ?: emptyList()
    }

    val activeTextClips = remember(project, playheadMs) {
        project?.tracks
            ?.filter { it.type == TrackType.TEXT }
            ?.flatMap { track ->
                track.clips.filter { playheadMs >= it.startMs && playheadMs < (it.startMs + it.durationMs) }
            } ?: emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF090A0F)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player Viewport Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val maxH = maxHeight
            val maxW = maxWidth

            Box(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .aspectRatio(ratio)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10121A))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.8f, 3.0f)
                            panOffset += pan
                        }
                    }
                    .testTag("preview_player_viewport"),
                contentAlignment = Alignment.Center
            ) {
                // Multi-layer Video Frame Rendering Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 1. Base Studio Canvas
                    drawRect(Color(0xFF161822))

                    // 2. Active Video Clips rendering
                    if (activeVideoClips.isNotEmpty()) {
                        activeVideoClips.forEach { clip ->
                            val baseColor = Color(clip.previewColorHex)
                            val cg = clip.colorGrading

                            // Color grade tint
                            val adjustedColor = when (cg.lutFilter) {
                                "Teal & Orange" -> Color(0xFF00ADB5)
                                "Cyberpunk" -> Color(0xFFE056FD)
                                "Film Noir" -> Color(0xFF888888)
                                "Warm Sunset" -> Color(0xFFFF7675)
                                "Retro VHS" -> Color(0xFFFDCB6E)
                                else -> baseColor
                            }

                            // Dynamic movement simulation based on playhead
                            val progress = ((playheadMs - clip.startMs).toFloat() / clip.durationMs.toFloat()).coerceIn(0f, 1f)
                            val pulse = (sin((playheadMs / 180.0)).toFloat() * 0.05f) * clip.scale

                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        adjustedColor.copy(alpha = clip.opacity * (1f + cg.brightness.coerceIn(-0.3f, 0.3f))),
                                        Color(0xFF0A0B10)
                                    )
                                )
                            )

                            // Active effect overlays
                            when {
                                clip.activeEffect.contains("Glitch", ignoreCase = true) -> {
                                    val glitchOffset = (sin(playheadMs.toDouble()) * 20f).toFloat()
                                    drawRect(
                                        color = Color(0x3300FFFF),
                                        topLeft = Offset(glitchOffset, 0f),
                                        size = Size(w, h)
                                    )
                                    drawRect(
                                        color = Color(0x33FF0055),
                                        topLeft = Offset(-glitchOffset, 0f),
                                        size = Size(w, h)
                                    )
                                }
                                clip.activeEffect.contains("Glow", ignoreCase = true) || clip.activeEffect.contains("Light", ignoreCase = true) -> {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            listOf(Color(0x66FFEA00), Color.Transparent),
                                            center = Offset(w * 0.5f, h * 0.3f),
                                            radius = w * 0.6f
                                        )
                                    )
                                }
                                clip.activeEffect.contains("Film", ignoreCase = true) || clip.activeEffect.contains("VHS", ignoreCase = true) -> {
                                    // Scanlines
                                    for (y in 0 until h.toInt() step 12) {
                                        drawLine(
                                            color = Color(0x22FFFFFF),
                                            start = Offset(0f, y.toFloat()),
                                            end = Offset(w, y.toFloat()),
                                            strokeWidth = 1f
                                        )
                                    }
                                }
                            }

                            // Subtle Vignette if configured
                            if (cg.vignette > 0f) {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        listOf(Color.Transparent, Color(0xCC000000)),
                                        center = Offset(w * 0.5f, h * 0.5f),
                                        radius = w * 0.7f
                                    )
                                )
                            }

                            // Film strip grid frame decoration
                            drawRect(
                                color = Color(0x22FFFFFF),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    } else {
                        // Empty timeline guide
                        drawCircle(
                            color = Color(0x22FFFFFF),
                            radius = 48.dp.toPx(),
                            center = Offset(w * 0.5f, h * 0.5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // 3. Text Overlay Composables
                if (activeTextClips.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        activeTextClips.forEach { clip ->
                            clip.textStyle?.let { ts ->
                                Surface(
                                    color = Color(0x88000000),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ts.text,
                                        color = Color(ts.textColor),
                                        fontSize = ts.fontSizeSp.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Aspect Ratio watermark badge
                Surface(
                    color = Color(0x66000000),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = aspect.label.substringBefore(" "),
                        color = Color(0xCCFFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Transport Controls Row (Play, Pause, Step frame, Timestamp, Mute)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timestamp: 00:03:12 / 00:12:00
            val currentSec = playheadMs / 1000
            val currentMsRemainder = (playheadMs % 1000) / 10
            val totalSec = (project?.durationMs ?: 0L) / 1000

            Text(
                text = String.format("%02d:%02d.%02d / %02d:00", currentSec / 60, currentSec % 60, currentMsRemainder, totalSec),
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            // Transport Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onStepFrame(false) },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("step_backward_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Step Back 1 Frame",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onPlayPauseToggle,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7C4DFF))
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { onStepFrame(true) },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("step_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Step Forward 1 Frame",
                        tint = Color.White
                    )
                }
            }

            // Mute / Volume toggle
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("mute_toggle_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = if (isMuted) Color(0xFFFF5252) else Color(0xFFA0A5B5)
                )
            }
        }
    }
}
