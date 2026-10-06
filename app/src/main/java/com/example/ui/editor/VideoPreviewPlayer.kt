package com.example.ui.editor

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.AspectRatioType
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TrackType
import java.util.Locale

@Composable
fun VideoPreviewPlayer(
    project: Project?,
    playheadMs: Long,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onToggleMute: () -> Unit,
    isMuted: Boolean,
    onImportMediaClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aspect = project?.aspectRatio ?: AspectRatioType.PORTRAIT_9_16
    val ratio = aspect.ratioWidth / aspect.ratioHeight

    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Find currently active video / overlay clip at playhead
    val activeVideoClip = remember(project, playheadMs) {
        project?.tracks
            ?.filter { it.type == TrackType.VIDEO || it.type == TrackType.OVERLAY }
            ?.flatMap { it.clips }
            ?.find { playheadMs >= it.startMs && playheadMs < (it.startMs + it.durationMs) }
    }

    // Active text clips
    val activeTextClips = remember(project, playheadMs) {
        project?.tracks
            ?.filter { it.type == TrackType.TEXT }
            ?.flatMap { it.clips }
            ?.filter { playheadMs >= it.startMs && playheadMs < (it.startMs + it.durationMs) }
            ?: emptyList()
    }

    // Active audio clip at playhead
    val activeAudioClip = remember(project, playheadMs) {
        project?.tracks
            ?.filter { it.type == TrackType.AUDIO }
            ?.flatMap { it.clips }
            ?.find { playheadMs >= it.startMs && playheadMs < (it.startMs + it.durationMs) }
    }

    // MediaPlayer state
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentLoadedUri by remember { mutableStateOf<String?>(null) }
    var currentLoadedAudioUri by remember { mutableStateOf<String?>(null) }
    var textureSurface by remember { mutableStateOf<Surface?>(null) }
    var isPlayerPrepared by remember { mutableStateOf(false) }
    var isAudioPlayerPrepared by remember { mutableStateOf(false) }

    // Manage MediaPlayer lifecycle
    DisposableEffect(Unit) {
        val player = MediaPlayer()
        val aPlayer = MediaPlayer()
        mediaPlayer = player
        audioPlayer = aPlayer
        onDispose {
            try {
                player.stop()
                player.reset()
                player.release()
            } catch (_: Exception) {}
            try {
                aPlayer.stop()
                aPlayer.reset()
                aPlayer.release()
            } catch (_: Exception) {}
            mediaPlayer = null
            audioPlayer = null
            textureSurface?.release()
        }
    }

    // Synchronize media loading for video
    val targetUri = activeVideoClip?.mediaUri
    LaunchedEffect(targetUri, textureSurface) {
        val player = mediaPlayer ?: return@LaunchedEffect
        val surface = textureSurface

        if (targetUri != null && targetUri != currentLoadedUri && surface != null) {
            try {
                isPlayerPrepared = false
                player.reset()
                player.setSurface(surface)
                player.setDataSource(context, Uri.parse(targetUri))
                player.setOnPreparedListener {
                    isPlayerPrepared = true
                    currentLoadedUri = targetUri
                    // Seek to initial offset
                    val clipOffset = (playheadMs - (activeVideoClip?.startMs ?: 0L) + (activeVideoClip?.trimStartMs ?: 0L)).coerceAtLeast(0L)
                    player.seekTo(clipOffset.toInt())
                    if (isPlaying) {
                        player.start()
                    }
                }
                player.prepareAsync()
            } catch (e: Exception) {
                isPlayerPrepared = false
            }
        } else if (targetUri == null && currentLoadedUri != null) {
            try {
                player.pause()
            } catch (_: Exception) {}
        }
    }

    // Synchronize media loading for audio track
    val targetAudioUri = activeAudioClip?.mediaUri
    LaunchedEffect(targetAudioUri) {
        val aPlayer = audioPlayer ?: return@LaunchedEffect
        if (targetAudioUri != null && targetAudioUri != currentLoadedAudioUri) {
            try {
                isAudioPlayerPrepared = false
                aPlayer.reset()
                aPlayer.setDataSource(context, Uri.parse(targetAudioUri))
                aPlayer.setOnPreparedListener {
                    isAudioPlayerPrepared = true
                    currentLoadedAudioUri = targetAudioUri
                    val offset = (playheadMs - (activeAudioClip?.startMs ?: 0L) + (activeAudioClip?.trimStartMs ?: 0L)).coerceAtLeast(0L)
                    aPlayer.seekTo(offset.toInt())
                    if (isPlaying) aPlayer.start()
                }
                aPlayer.prepareAsync()
            } catch (_: Exception) {
                isAudioPlayerPrepared = false
            }
        } else if (targetAudioUri == null && currentLoadedAudioUri != null) {
            try { aPlayer.pause() } catch (_: Exception) {}
        }
    }

    // Synchronize audio track playback and volume
    LaunchedEffect(playheadMs, isPlaying, isMuted, activeAudioClip, isAudioPlayerPrepared) {
        val aPlayer = audioPlayer ?: return@LaunchedEffect
        if (!isAudioPlayerPrepared) return@LaunchedEffect
        val clip = activeAudioClip ?: run {
            try { if (aPlayer.isPlaying) aPlayer.pause() } catch (_: Exception) {}
            return@LaunchedEffect
        }
        val offset = ((playheadMs - clip.startMs) * clip.speed + clip.trimStartMs).toLong().coerceAtLeast(0L)
        try {
            val vol = if (isMuted || clip.isMuted) 0f else clip.volume.coerceIn(0f, 2f)
            aPlayer.setVolume(vol, vol)
            if (isPlaying) {
                if (!aPlayer.isPlaying) {
                    aPlayer.seekTo(offset.toInt())
                    aPlayer.start()
                }
            } else {
                if (aPlayer.isPlaying) aPlayer.pause()
                aPlayer.seekTo(offset.toInt())
            }
        } catch (_: Exception) {}
    }

    // Synchronize playhead seeking and playback
    LaunchedEffect(playheadMs, isPlaying, isMuted, activeVideoClip) {
        val player = mediaPlayer ?: return@LaunchedEffect
        if (!isPlayerPrepared) return@LaunchedEffect

        val clip = activeVideoClip ?: return@LaunchedEffect
        val clipOffset = ((playheadMs - clip.startMs) * clip.speed + clip.trimStartMs).toLong().coerceAtLeast(0L)

        try {
            // Apply volume / mute
            val vol = if (isMuted || clip.isMuted) 0f else clip.volume.coerceIn(0f, 2f)
            player.setVolume(vol, vol)

            // Apply playback speed
            try {
                val params = PlaybackParams().setSpeed(clip.speed.coerceIn(0.2f, 5.0f))
                player.playbackParams = params
            } catch (_: Exception) {}

            // Sync play / pause
            if (isPlaying) {
                if (!player.isPlaying) {
                    player.seekTo(clipOffset.toInt())
                    player.start()
                }
            } else {
                if (player.isPlaying) {
                    player.pause()
                }
                player.seekTo(clipOffset.toInt())
            }
        } catch (_: Exception) {}
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
                // If there's an active video clip with real mediaUri
                if (activeVideoClip?.mediaUri != null && activeVideoClip.type == TrackType.VIDEO) {
                    AndroidView(
                        factory = { ctx ->
                            TextureView(ctx).apply {
                                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                                        val s = Surface(st)
                                        textureSurface = s
                                        mediaPlayer?.setSurface(s)
                                    }
                                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, width: Int, height: Int) {}
                                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                        textureSurface?.release()
                                        textureSurface = null
                                        mediaPlayer?.setSurface(null)
                                        return true
                                    }
                                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = activeVideoClip.rotation
                                scaleX = activeVideoClip.scale * (if (activeVideoClip.isFlipHorizontal) -1f else 1f) * zoomScale
                                scaleY = activeVideoClip.scale * (if (activeVideoClip.isFlipVertical) -1f else 1f) * zoomScale
                                translationX = panOffset.x
                                translationY = panOffset.y
                                alpha = activeVideoClip.opacity
                            }
                    )
                } else if (activeVideoClip?.mediaUri != null) {
                    // Photo / Image clip with mediaUri
                    AsyncImage(
                        model = activeVideoClip.mediaUri,
                        contentDescription = activeVideoClip.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = activeVideoClip.rotation
                                scaleX = activeVideoClip.scale * (if (activeVideoClip.isFlipHorizontal) -1f else 1f) * zoomScale
                                scaleY = activeVideoClip.scale * (if (activeVideoClip.isFlipVertical) -1f else 1f) * zoomScale
                                translationX = panOffset.x
                                translationY = panOffset.y
                                alpha = activeVideoClip.opacity
                            }
                    )
                } else {
                    // Empty or generator canvas placeholder
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(Color(0xFF131522))
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF7C4DFF),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = if (activeVideoClip != null) activeVideoClip.title else "No Clip at Playhead",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Color Grade Overlay Tint
                activeVideoClip?.colorGrading?.let { cg ->
                    val overlayColor = when (cg.lutFilter) {
                        "Teal & Orange" -> Color(0x3300ADB5)
                        "Cyberpunk" -> Color(0x33E056FD)
                        "Film Noir" -> Color(0x66000000)
                        "Warm Sunset" -> Color(0x33FF7675)
                        "Retro VHS" -> Color(0x33FDCB6E)
                        else -> Color.Transparent
                    }
                    if (overlayColor != Color.Transparent) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(overlayColor)
                        )
                    }
                }

                // Text & Animated Captions Layer
                activeTextClips.forEach { textClip ->
                    val style = textClip.textStyle
                    val textStr = style?.text ?: textClip.title
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = textStr,
                                color = Color(style?.textColor ?: 0xFFFFFFFF),
                                fontSize = (style?.fontSizeSp ?: 20f).sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Aspect Ratio indicator badge at top right
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = aspect.label.substringBefore(" "),
                            color = Color(0xFFA0A5B5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Playback Transport Controls Bar
        Surface(
            color = Color(0xFF10121A),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playhead Timestamp
                val totalMs = project?.durationMs ?: 0L
                Text(
                    text = "${formatTimecode(playheadMs)} / ${formatTimecode(totalMs)}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // Central transport buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Step back 1 frame (-33ms)
                    IconButton(
                        onClick = { onStepFrame(false) },
                        modifier = Modifier.size(36.dp).testTag("step_backward_button")
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Step -1 Frame", tint = Color(0xFFA0A5B5), modifier = Modifier.size(20.dp))
                    }

                    // Main Play/Pause Button
                    IconButton(
                        onClick = onPlayPauseToggle,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7C4DFF))
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Step forward 1 frame (+33ms)
                    IconButton(
                        onClick = { onStepFrame(true) },
                        modifier = Modifier.size(36.dp).testTag("step_forward_button")
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Step +1 Frame", tint = Color(0xFFA0A5B5), modifier = Modifier.size(20.dp))
                    }
                }

                // Volume / Mute toggle
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(36.dp).testTag("mute_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute",
                        tint = if (isMuted) Color(0xFFFF5252) else Color(0xFFA0A5B5),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millisFraction = (ms % 1000) / 100
    return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, millisFraction)
}
