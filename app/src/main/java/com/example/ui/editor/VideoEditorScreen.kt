package com.example.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.model.ColorGradingSettings
import com.example.ui.export.ExportSheet
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun VideoEditorScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val activeProject by viewModel.activeProject.collectAsState()
    val playheadMs by viewModel.playheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val timelineZoom by viewModel.timelineZoom.collectAsState()
    val exportState by viewModel.exportState.collectAsState()

    var showEffectsModal by remember { mutableStateOf(false) }
    var showTransitionsModal by remember { mutableStateOf(false) }
    var showColorModal by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            EditorTopBar(
                projectTitle = activeProject?.title ?: "AI Studio Project",
                onBack = onNavigateBack,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onOpenExport = { showExportSheet = true }
            )
        },
        bottomBar = {
            EditorBottomActionToolbar(
                isClipSelected = selectedClipId != null,
                onSplit = { viewModel.splitClipAtPlayhead() },
                onDelete = { viewModel.deleteSelectedClip() },
                onDuplicate = { viewModel.duplicateSelectedClip() },
                onOpenSpeed = { viewModel.updateSelectedClipSpeed(1.5f) },
                onOpenVolume = { viewModel.toggleSelectedClipMute() },
                onOpenFilters = { showColorModal = true },
                onOpenEffects = { showEffectsModal = true },
                onOpenTransitions = { showTransitionsModal = true },
                onAddText = { viewModel.addTextOverlay("New AI Caption") },
                onToggleAiBgRemove = { viewModel.toggleSelectedClipBackgroundRemoval() },
                onToggleChromaKey = { viewModel.toggleSelectedClipChromaKey() }
            )
        },
        containerColor = Color(0xFF090A0F)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Upper Half: Video Preview Player (Real-time Canvas Rendering)
            VideoPreviewPlayer(
                project = activeProject,
                playheadMs = playheadMs,
                isPlaying = isPlaying,
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onStepFrame = { forward -> viewModel.stepFrame(forward) },
                onToggleMute = { /* mute main */ },
                isMuted = false,
                modifier = Modifier.weight(1.0f)
            )

            // Lower Half: Multi-layer Timeline
            TimelineView(
                project = activeProject,
                playheadMs = playheadMs,
                zoomScale = timelineZoom,
                selectedClipId = selectedClipId,
                onSeekTo = { timeMs -> viewModel.seekTo(timeMs) },
                onSelectClip = { clipId, trackId -> viewModel.selectClip(clipId, trackId) },
                modifier = Modifier.weight(1.1f)
            )
        }
    }

    // Modal Sheets
    if (showEffectsModal) {
        EffectsGalleryModal(
            onDismiss = { showEffectsModal = false },
            onSelectEffect = { effectName -> viewModel.updateSelectedClipEffect(effectName) }
        )
    }

    if (showTransitionsModal) {
        TransitionsModal(
            onDismiss = { showTransitionsModal = false },
            onSelectTransition = { trName, dur -> viewModel.updateSelectedClipTransition(trName, dur) }
        )
    }

    if (showColorModal) {
        val currentSettings = activeProject?.tracks
            ?.flatMap { it.clips }
            ?.find { it.id == selectedClipId }?.colorGrading ?: ColorGradingSettings()

        ColorGradingModal(
            currentSettings = currentSettings,
            onDismiss = { showColorModal = false },
            onApplySettings = { settings -> viewModel.updateSelectedClipColorSettings(settings) }
        )
    }

    if (showExportSheet && activeProject != null) {
        ExportSheet(
            project = activeProject!!,
            exportState = exportState,
            onStartExport = { options -> viewModel.startExport(options) },
            onCancelExport = { viewModel.cancelExport() },
            onDismiss = {
                viewModel.resetExportState()
                showExportSheet = false
            }
        )
    }
}
