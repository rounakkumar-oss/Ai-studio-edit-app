package com.example.ui.editor

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.ColorGradingSettings
import com.example.data.model.TrackType
import com.example.ui.export.ExportSheet
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun VideoEditorScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
    var showSpeedModal by remember { mutableStateOf(false) }
    var showVolumeModal by remember { mutableStateOf(false) }
    var showCropModal by remember { mutableStateOf(false) }
    var showAudioModal by remember { mutableStateOf(false) }
    var showAiModal by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }

    // Pick media for Overlay
    val pickOverlayLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importMediaAsClip(
                title = "Overlay Clip",
                uri = uri,
                type = TrackType.OVERLAY,
                durationMs = 3000L
            )
        }
    }

    // Pick music for Audio track
    val pickMusicLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importMediaAsClip(
                title = "BGM Audio",
                uri = uri,
                type = TrackType.AUDIO,
                durationMs = 5000L
            )
        }
    }

    val selectedClip = remember(activeProject, selectedClipId) {
        activeProject?.tracks?.flatMap { it.clips }?.find { it.id == selectedClipId }
    }

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
            if (selectedClipId != null) {
                // Clip-specific editing controls (CapCut style)
                EditorClipSpecificToolbar(
                    onDeselect = { viewModel.selectClip(null, null) },
                    onSplit = { viewModel.splitClipAtPlayhead() },
                    onTrimLeft = { viewModel.trimSelectedClipToPlayhead(true) },
                    onTrimRight = { viewModel.trimSelectedClipToPlayhead(false) },
                    onOpenSpeed = { showSpeedModal = true },
                    onOpenVolume = { showVolumeModal = true },
                    onOpenCrop = { showCropModal = true },
                    onRotate = { viewModel.rotateSelectedClip() },
                    onFlipH = { viewModel.flipSelectedClipHorizontal() },
                    onFlipV = { viewModel.flipSelectedClipVertical() },
                    onResize = { viewModel.resizeSelectedClip(1.2f) },
                    onFreeze = { viewModel.freezeFrameAtPlayhead() },
                    onReverse = { viewModel.reverseSelectedClip() },
                    onExtractAudio = { viewModel.extractAudioFromSelectedClip() },
                    onDuplicate = { viewModel.duplicateSelectedClip() },
                    onMoveLeft = { viewModel.reorderSelectedClip(true) },
                    onMoveRight = { viewModel.reorderSelectedClip(false) },
                    onDelete = { viewModel.deleteSelectedClip() }
                )
            } else {
                // CapCut main bottom toolbar: Edit, Audio, Text, Overlay, Effects, Transitions, Filters, Speed, Adjust, AI
                EditorMainBottomToolbar(
                    onOpenEdit = {
                        val firstClipId = activeProject?.tracks?.firstOrNull()?.clips?.firstOrNull()?.id
                        viewModel.selectClip(firstClipId, null)
                    },
                    onOpenAudio = { showAudioModal = true },
                    onOpenText = { viewModel.addTextOverlay("New Caption") },
                    onOpenOverlay = {
                        pickOverlayLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                    onOpenEffects = { showEffectsModal = true },
                    onOpenTransitions = { showTransitionsModal = true },
                    onOpenFilters = { showColorModal = true },
                    onOpenSpeed = { showSpeedModal = true },
                    onOpenAdjust = { showColorModal = true },
                    onOpenAI = { showAiModal = true }
                )
            }
        },
        containerColor = Color(0xFF090A0F)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Upper Half: Video Preview Player (Real-time Canvas & TextureView Rendering)
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

            // Lower Half: Multi-layer Timeline with drag handles and seek
            TimelineView(
                project = activeProject,
                playheadMs = playheadMs,
                zoomScale = timelineZoom,
                selectedClipId = selectedClipId,
                onSeekTo = { timeMs -> viewModel.seekTo(timeMs) },
                onSelectClip = { clipId, trackId -> viewModel.selectClip(clipId, trackId) },
                onTrimStart = { deltaMs -> viewModel.trimSelectedClipStart(deltaMs) },
                onTrimEnd = { deltaMs -> viewModel.trimSelectedClipEnd(deltaMs) },
                modifier = Modifier.weight(1.1f)
            )
        }
    }

    // Modal Sheets
    if (showSpeedModal) {
        val currSpeed = selectedClip?.speed ?: 1.0f
        SpeedModal(
            currentSpeed = currSpeed,
            onDismiss = { showSpeedModal = false },
            onApplySpeed = { speed -> viewModel.updateSelectedClipSpeed(speed) }
        )
    }

    if (showVolumeModal) {
        val currVol = selectedClip?.volume ?: 1.0f
        val isMuted = selectedClip?.isMuted ?: false
        VolumeModal(
            currentVolume = currVol,
            isMuted = isMuted,
            onDismiss = { showVolumeModal = false },
            onApplyVolume = { vol -> viewModel.updateSelectedClipVolume(vol) },
            onToggleMute = { viewModel.toggleSelectedClipMute() }
        )
    }

    if (showCropModal) {
        CropAspectModal(
            onDismiss = { showCropModal = false },
            onSelectRatio = { ratio -> viewModel.cropSelectedClip(ratio) }
        )
    }

    if (showAudioModal) {
        AudioToolsModal(
            onDismiss = { showAudioModal = false },
            onAddMusic = { pickMusicLauncher.launch("audio/*") },
            onAddVoiceover = { onNavigate("ai_voice") },
            onExtractAudio = { viewModel.extractAudioFromSelectedClip() },
            onOpenAiMusic = { onNavigate("ai_music") }
        )
    }

    if (showAiModal) {
        AiToolsModal(
            onDismiss = { showAiModal = false },
            onNavigate = onNavigate
        )
    }

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
        val currentSettings = selectedClip?.colorGrading ?: ColorGradingSettings()

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
