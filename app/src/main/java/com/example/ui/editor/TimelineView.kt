package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioEngine
import com.example.data.model.Project
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType

@Composable
fun TimelineView(
    project: Project?,
    playheadMs: Long,
    zoomScale: Float,
    selectedClipId: String?,
    onSeekTo: (Long) -> Unit,
    onSelectClip: (clipId: String?, trackId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (project == null) return

    val durationMs = project.durationMs.coerceAtLeast(1000L)
    // 60.dp per second as base scale
    val pxPerMs = (0.06f * zoomScale)
    val totalTimelineWidthDp = (durationMs * pxPerMs).dp.coerceAtLeast(600.dp)

    val scrollState = rememberScrollState()

    // Auto-scroll timeline to follow playhead during playback
    LaunchedEffect(playheadMs) {
        val playheadOffsetDp = (playheadMs * pxPerMs).dp
        // smooth sync if out of view
        if (playheadOffsetDp > 300.dp) {
            scrollState.scrollTo((playheadOffsetDp.value * 2.2f).toInt())
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F111A))
    ) {
        // Track Header Column (Left pane: icons, track types)
        Column(
            modifier = Modifier
                .width(52.dp)
                .background(Color(0xFF141724))
                .border(width = 1.dp, color = Color(0xFF222638))
        ) {
            // Ruler Corner Spacer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(Color(0xFF181B2A))
            )

            // Track icons stack
            project.tracks.forEach { track ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = getTrackIcon(track.type)
                    val iconTint = getTrackTint(track.type)
                    Icon(
                        imageVector = icon,
                        contentDescription = track.name,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Horizontal Scrollable Timeline Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(scrollState)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val currentMs = playheadMs
                        val deltaMs = (-delta / pxPerMs).toLong()
                        onSeekTo((currentMs + deltaMs).coerceIn(0L, durationMs))
                    }
                )
                .testTag("timeline_scroll_area")
        ) {
            Column(
                modifier = Modifier
                    .width(totalTimelineWidthDp + 200.dp)
                    .fillMaxHeight()
            ) {
                // 1. Time Ruler (Top bar with seconds markers)
                TimelineRuler(
                    durationMs = durationMs,
                    pxPerMs = pxPerMs,
                    onSeekTo = onSeekTo
                )

                // 2. Track Lanes Stack
                project.tracks.forEach { track ->
                    TimelineTrackLane(
                        track = track,
                        pxPerMs = pxPerMs,
                        selectedClipId = selectedClipId,
                        onSelectClip = { clipId -> onSelectClip(clipId, track.id) }
                    )
                }
            }

            // 3. Vertical Playhead Line Indicator
            val playheadX = (playheadMs * pxPerMs).dp
            Box(
                modifier = Modifier
                    .offset(x = playheadX)
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF00E5FF))
            ) {
                // Playhead needle header handle
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .offset(x = (-5).dp, y = 8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                )
            }
        }
    }
}

@Composable
fun TimelineRuler(
    durationMs: Long,
    pxPerMs: Float,
    onSeekTo: (Long) -> Unit
) {
    val totalSeconds = (durationMs / 1000).toInt() + 1
    val totalWidth = (durationMs * pxPerMs).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(Color(0xFF141724))
            .clickable { /* Tap to seek */ }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height

            for (sec in 0..totalSeconds) {
                val x = (sec * 1000L * pxPerMs)

                // Major second tick
                drawLine(
                    color = Color(0xFF6B728E),
                    start = Offset(x, h - 14f),
                    end = Offset(x, h),
                    strokeWidth = 1.5f
                )

                // Half second tick
                val halfX = x + (500L * pxPerMs)
                drawLine(
                    color = Color(0xFF474E68),
                    start = Offset(halfX, h - 8f),
                    end = Offset(halfX, h),
                    strokeWidth = 1.0f
                )
            }
        }

        // Second Labels
        Row(modifier = Modifier.fillMaxSize()) {
            for (sec in 0..totalSeconds step 2) {
                val x = (sec * 1000L * pxPerMs).dp
                Text(
                    text = String.format("%02d:%02d", sec / 60, sec % 60),
                    color = Color(0xFFA0A5B5),
                    fontSize = 10.sp,
                    modifier = Modifier.offset(x = x + 4.dp, y = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TimelineTrackLane(
    track: TimelineTrack,
    pxPerMs: Float,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFF161926))
            .border(width = 0.5.dp, color = Color(0xFF222638))
    ) {
        // Clips in this lane
        track.clips.forEach { clip ->
            val startX = (clip.startMs * pxPerMs).dp
            val clipWidth = (clip.durationMs * pxPerMs).dp.coerceAtLeast(36.dp)
            val isSelected = clip.id == selectedClipId

            TimelineClipBlock(
                clip = clip,
                isSelected = isSelected,
                modifier = Modifier
                    .offset(x = startX)
                    .width(clipWidth)
                    .height(44.dp)
                    .padding(vertical = 4.dp),
                onSelect = { onSelectClip(clip.id) }
            )
        }
    }
}

@Composable
fun TimelineClipBlock(
    clip: TimelineClip,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blockColor = when (clip.type) {
        TrackType.VIDEO -> Color(clip.previewColorHex)
        TrackType.OVERLAY -> Color(0xFFE67E22)
        TrackType.AUDIO -> Color(0xFF00ADB5)
        TrackType.TEXT -> Color(0xFFFF2E93)
        TrackType.EFFECT -> Color(0xFF9B59B6)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(blockColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0x44FFFFFF),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onSelect() }
            .testTag("clip_block_${clip.id}"),
        contentAlignment = Alignment.CenterStart
    ) {
        // Audio Waveform background pattern if Audio Track
        if (clip.type == TrackType.AUDIO) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val barCount = (w / 8f).toInt().coerceAtLeast(5)
                val bars = AudioEngine.generateWaveformBars(clip.durationMs, barCount)

                bars.forEachIndexed { idx, amp ->
                    val barX = idx * 8f
                    val barH = h * amp * 0.7f
                    drawLine(
                        color = Color(0x66FFFFFF),
                        start = Offset(barX, (h - barH) / 2f),
                        end = Offset(barX, (h + barH) / 2f),
                        strokeWidth = 3f
                    )
                }
            }
        }

        // Clip Title & Badges
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = clip.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            // Transition or Effect pill tag if active
            if (clip.transitionIn != "None") {
                Surface(
                    color = Color(0x88000000),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "⚡ ${clip.transitionIn.take(6)}",
                        color = Color(0xFFFFD700),
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun getTrackIcon(type: TrackType): ImageVector = when (type) {
    TrackType.VIDEO -> Icons.Default.Videocam
    TrackType.OVERLAY -> Icons.Default.Layers
    TrackType.AUDIO -> Icons.Default.Audiotrack
    TrackType.TEXT -> Icons.Default.TextFields
    TrackType.EFFECT -> Icons.Default.AutoAwesome
}

private fun getTrackTint(type: TrackType): Color = when (type) {
    TrackType.VIDEO -> Color(0xFF7C4DFF)
    TrackType.OVERLAY -> Color(0xFFE67E22)
    TrackType.AUDIO -> Color(0xFF00ADB5)
    TrackType.TEXT -> Color(0xFFFF2E93)
    TrackType.EFFECT -> Color(0xFF9B59B6)
}
