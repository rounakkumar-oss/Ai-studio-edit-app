package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorTopBar(
    projectTitle: String,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF0B0C12))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = projectTitle,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.width(140.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onUndo,
                modifier = Modifier.testTag("undo_button").size(38.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color(0xFFA0A5B5), modifier = Modifier.size(20.dp))
            }

            IconButton(
                onClick = onRedo,
                modifier = Modifier.testTag("redo_button").size(38.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = Color(0xFFA0A5B5), modifier = Modifier.size(20.dp))
            }

            Button(
                onClick = onOpenExport,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("export_button")
            ) {
                Text(
                    text = "Export",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * CapCut-Like Main Bottom Toolbar with requested categories:
 * Edit, Audio, Text, Overlay, Effects, Transitions, Filters, Speed, Adjust, AI
 */
@Composable
fun EditorMainBottomToolbar(
    onOpenEdit: () -> Unit,
    onOpenAudio: () -> Unit,
    onOpenText: () -> Unit,
    onOpenOverlay: () -> Unit,
    onOpenEffects: () -> Unit,
    onOpenTransitions: () -> Unit,
    onOpenFilters: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenAdjust: () -> Unit,
    onOpenAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF131522))
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorToolActionItem(
            icon = Icons.Default.Edit,
            label = "Edit",
            onClick = onOpenEdit
        )
        EditorToolActionItem(
            icon = Icons.Default.Audiotrack,
            label = "Audio",
            tint = Color(0xFF00ADB5),
            onClick = onOpenAudio
        )
        EditorToolActionItem(
            icon = Icons.Default.TextFields,
            label = "Text",
            tint = Color(0xFFFF2E93),
            onClick = onOpenText
        )
        EditorToolActionItem(
            icon = Icons.Default.Layers,
            label = "Overlay",
            tint = Color(0xFFFFB300),
            onClick = onOpenOverlay
        )
        EditorToolActionItem(
            icon = Icons.Default.AutoAwesome,
            label = "Effects",
            tint = Color(0xFFFF5370),
            onClick = onOpenEffects
        )
        EditorToolActionItem(
            icon = Icons.Default.Transform,
            label = "Transitions",
            tint = Color(0xFF00E5FF),
            onClick = onOpenTransitions
        )
        EditorToolActionItem(
            icon = Icons.Default.Palette,
            label = "Filters",
            tint = Color(0xFF2ED573),
            onClick = onOpenFilters
        )
        EditorToolActionItem(
            icon = Icons.Default.Speed,
            label = "Speed",
            tint = Color(0xFFFFA502),
            onClick = onOpenSpeed
        )
        EditorToolActionItem(
            icon = Icons.Default.Tune,
            label = "Adjust",
            tint = Color(0xFF70A1FF),
            onClick = onOpenAdjust
        )
        EditorToolActionItem(
            icon = Icons.Default.AutoAwesome,
            label = "AI",
            tint = Color(0xFFE056FD),
            onClick = onOpenAI
        )
    }
}

/**
 * When a clip is selected, CapCut displays clip-specific editing controls:
 * Split, Trim Left, Trim Right, Speed, Volume, Crop, Rotate, Flip, Resize, Freeze, Reverse, Extract Audio, Duplicate, Move, Delete
 */
@Composable
fun EditorClipSpecificToolbar(
    onDeselect: () -> Unit,
    onSplit: () -> Unit,
    onTrimLeft: () -> Unit,
    onTrimRight: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenVolume: () -> Unit,
    onOpenCrop: () -> Unit,
    onRotate: () -> Unit,
    onFlipH: () -> Unit,
    onFlipV: () -> Unit,
    onResize: () -> Unit,
    onFreeze: () -> Unit,
    onReverse: () -> Unit,
    onExtractAudio: () -> Unit,
    onDuplicate: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF10121C))
            .border(width = 1.dp, color = Color(0xFF25293E))
            .horizontalScroll(scrollState)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Done / Close sub-mode button
        Column(
            modifier = Modifier
                .size(width = 54.dp, height = 60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF25293E))
                .clickable { onDeselect() }
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = "Done", tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
            Text("Done", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
        }

        EditorToolActionItem(
            icon = Icons.Default.CallSplit,
            label = "Split",
            onClick = onSplit
        )

        EditorToolActionItem(
            icon = Icons.Default.Crop,
            label = "Trim Left",
            onClick = onTrimLeft
        )

        EditorToolActionItem(
            icon = Icons.Default.Crop,
            label = "Trim Right",
            onClick = onTrimRight
        )

        EditorToolActionItem(
            icon = Icons.Default.Speed,
            label = "Speed",
            tint = Color(0xFFFFA502),
            onClick = onOpenSpeed
        )

        EditorToolActionItem(
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            label = "Volume",
            tint = Color(0xFF00ADB5),
            onClick = onOpenVolume
        )

        EditorToolActionItem(
            icon = Icons.Default.Crop,
            label = "Crop",
            onClick = onOpenCrop
        )

        EditorToolActionItem(
            icon = Icons.Default.RotateRight,
            label = "Rotate",
            onClick = onRotate
        )

        EditorToolActionItem(
            icon = Icons.Default.Flip,
            label = "Flip H",
            onClick = onFlipH
        )

        EditorToolActionItem(
            icon = Icons.Default.Flip,
            label = "Flip V",
            onClick = onFlipV
        )

        EditorToolActionItem(
            icon = Icons.Default.ZoomIn,
            label = "Resize",
            onClick = onResize
        )

        EditorToolActionItem(
            icon = Icons.Default.AcUnit,
            label = "Freeze",
            tint = Color(0xFF70A1FF),
            onClick = onFreeze
        )

        EditorToolActionItem(
            icon = Icons.Default.Transform,
            label = "Reverse",
            onClick = onReverse
        )

        EditorToolActionItem(
            icon = Icons.Default.Audiotrack,
            label = "Extract",
            tint = Color(0xFF00ADB5),
            onClick = onExtractAudio
        )

        EditorToolActionItem(
            icon = Icons.Default.ContentCopy,
            label = "Duplicate",
            onClick = onDuplicate
        )

        EditorToolActionItem(
            icon = Icons.Default.FastForward,
            label = "Move ◀",
            onClick = onMoveLeft
        )

        EditorToolActionItem(
            icon = Icons.Default.FastForward,
            label = "Move ▶",
            onClick = onMoveRight
        )

        EditorToolActionItem(
            icon = Icons.Default.Delete,
            label = "Delete",
            tint = Color(0xFFFF5252),
            onClick = onDelete
        )
    }
}

@Composable
fun EditorToolActionItem(
    icon: ImageVector,
    label: String,
    tint: Color = Color.White,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val alpha = if (isEnabled) 1.0f else 0.35f

    Column(
        modifier = Modifier
            .size(width = 60.dp, height = 60.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled) { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint.copy(alpha = alpha),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = Color(0xFFA0A5B5).copy(alpha = alpha),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
