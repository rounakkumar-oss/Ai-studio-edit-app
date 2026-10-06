package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onBack() }
            )
            Text(
                text = projectTitle,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onUndo,
                modifier = Modifier.testTag("undo_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color(0xFFA0A5B5))
            }

            IconButton(
                onClick = onRedo,
                modifier = Modifier.testTag("redo_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = Color(0xFFA0A5B5))
            }

            // High-visibility Export Button
            androidx.compose.material3.Button(
                onClick = onOpenExport,
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C4DFF)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
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

@Composable
fun EditorBottomActionToolbar(
    isClipSelected: Boolean,
    onSplit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenVolume: () -> Unit,
    onOpenFilters: () -> Unit,
    onOpenEffects: () -> Unit,
    onOpenTransitions: () -> Unit,
    onAddText: () -> Unit,
    onToggleAiBgRemove: () -> Unit,
    onToggleChromaKey: () -> Unit,
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
            icon = Icons.Default.CallSplit,
            label = "Split",
            isEnabled = isClipSelected,
            onClick = onSplit
        )

        EditorToolActionItem(
            icon = Icons.Default.Speed,
            label = "Speed",
            isEnabled = isClipSelected,
            onClick = onOpenSpeed
        )

        EditorToolActionItem(
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            label = "Volume",
            isEnabled = isClipSelected,
            onClick = onOpenVolume
        )

        EditorToolActionItem(
            icon = Icons.Default.Palette,
            label = "Filter",
            isEnabled = isClipSelected,
            onClick = onOpenFilters
        )

        EditorToolActionItem(
            icon = Icons.Default.AutoAwesome,
            label = "Effects",
            onClick = onOpenEffects
        )

        EditorToolActionItem(
            icon = Icons.Default.Transform,
            label = "Transitions",
            onClick = onOpenTransitions
        )

        EditorToolActionItem(
            icon = Icons.Default.TextFields,
            label = "Text",
            onClick = onAddText
        )

        EditorToolActionItem(
            icon = Icons.Default.Flip,
            label = "AI Cutout",
            isEnabled = isClipSelected,
            onClick = onToggleAiBgRemove
        )

        EditorToolActionItem(
            icon = Icons.Default.RotateRight,
            label = "Chroma Key",
            isEnabled = isClipSelected,
            onClick = onToggleChromaKey
        )

        EditorToolActionItem(
            icon = Icons.Default.ContentCopy,
            label = "Duplicate",
            isEnabled = isClipSelected,
            onClick = onDuplicate
        )

        EditorToolActionItem(
            icon = Icons.Default.Delete,
            label = "Delete",
            tint = Color(0xFFFF5252),
            isEnabled = isClipSelected,
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
            .size(width = 62.dp, height = 60.dp)
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
