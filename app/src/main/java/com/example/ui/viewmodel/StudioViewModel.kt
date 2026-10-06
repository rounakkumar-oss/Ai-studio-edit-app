package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIDirectorEngine
import com.example.ai.AIResult
import com.example.ai.AIScriptStoryboard
import com.example.ai.AIVideoGenerationSpec
import com.example.ai.CommandAction
import com.example.ai.GeminiAiService
import com.example.ai.GeneratedImageResult
import com.example.ai.GeneratedMusicResult
import com.example.ai.GeneratedVideoResult
import com.example.ai.GeneratedVoiceResult
import com.example.audio.AudioEngine
import com.example.audio.RealTtsEngine
import com.example.data.local.AppDatabase
import com.example.data.model.AspectRatioType
import com.example.data.model.CaptionItem
import com.example.data.model.ColorGradingSettings
import com.example.data.model.Project
import com.example.data.model.TemplateItem
import com.example.data.model.TextStyleModel
import com.example.data.model.TimelineClip
import com.example.data.model.TimelineTrack
import com.example.data.model.TrackType
import com.example.data.repository.ProjectRepository
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import com.example.updater.GitHubUpdateChecker
import com.example.updater.ReleaseInfo
import com.example.util.MediaUtils
import com.example.video.ExportEngine
import com.example.video.ExportOptions
import com.example.video.ExportState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class StudioUiEvent {
    data class ShowToast(val message: String) : StudioUiEvent()
    data class NavigateTo(val screenRoute: String) : StudioUiEvent()
    object OpenExportSheet : StudioUiEvent()
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ProjectRepository(db.projectDao())
    private val prefsRepository = UserPreferencesRepository(db.userPreferencesDao())
    val aiService = GeminiAiService()
    val exportEngine = ExportEngine(application)
    val ttsEngine = RealTtsEngine(application)

    // User preferences from Room
    val userPreferences: StateFlow<UserPreferences> = prefsRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setProxyMediaPreview(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setProxyMediaPreview(enabled) }
    }

    fun setHardwareAcceleration(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setHardwareAcceleration(enabled) }
    }

    fun setDefaultExportSettings(resolution: String, fps: Int) {
        viewModelScope.launch { prefsRepository.setExportDefaults(resolution, fps) }
    }

    // Projects list from Room
    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .let { flow ->
            val state = MutableStateFlow<List<Project>>(emptyList())
            viewModelScope.launch {
                flow.collectLatest { list ->
                    if (list.isEmpty()) {
                        // Seed starter project on first launch
                        val starter = ProjectRepository.createDefaultProject("Cyberpunk City Reel", AspectRatioType.PORTRAIT_9_16)
                        repository.saveProject(starter)
                    }
                    state.value = list
                }
            }
            state.asStateFlow()
        }

    val favoriteProjects: StateFlow<List<Project>> = repository.favoriteProjects
        .let { flow ->
            val state = MutableStateFlow<List<Project>>(emptyList())
            viewModelScope.launch { flow.collectLatest { state.value = it } }
            state.asStateFlow()
        }

    // Active project in editor
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<Project>()
    private val redoStack = mutableListOf<Project>()

    // Editor Playback State
    private val _playheadMs = MutableStateFlow(0L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _selectedTrackId = MutableStateFlow<String?>(null)
    val selectedTrackId: StateFlow<String?> = _selectedTrackId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5x to 3.0x
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private var playbackJob: Job? = null

    // Export State
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()
    private var exportJob: Job? = null

    // UI Events
    private val _uiEvents = MutableSharedFlow<StudioUiEvent>()
    val uiEvents: SharedFlow<StudioUiEvent> = _uiEvents.asSharedFlow()

    // Update Checker
    private val _releaseInfo = MutableStateFlow<ReleaseInfo?>(null)
    val releaseInfo: StateFlow<ReleaseInfo?> = _releaseInfo.asStateFlow()

    // AI Generation States
    private val _aiScriptState = MutableStateFlow<AIResult<AIScriptStoryboard>?>(null)
    val aiScriptState: StateFlow<AIResult<AIScriptStoryboard>?> = _aiScriptState.asStateFlow()

    private val _aiVideoState = MutableStateFlow<AIResult<GeneratedVideoResult>?>(null)
    val aiVideoState: StateFlow<AIResult<GeneratedVideoResult>?> = _aiVideoState.asStateFlow()

    private val _aiVoiceState = MutableStateFlow<AIResult<GeneratedVoiceResult>?>(null)
    val aiVoiceState: StateFlow<AIResult<GeneratedVoiceResult>?> = _aiVoiceState.asStateFlow()

    private val _aiMusicState = MutableStateFlow<AIResult<GeneratedMusicResult>?>(null)
    val aiMusicState: StateFlow<AIResult<GeneratedMusicResult>?> = _aiMusicState.asStateFlow()

    private val _aiImageState = MutableStateFlow<AIResult<GeneratedImageResult>?>(null)
    val aiImageState: StateFlow<AIResult<GeneratedImageResult>?> = _aiImageState.asStateFlow()

    private val _captionsList = MutableStateFlow<List<CaptionItem>>(emptyList())
    val captionsList: StateFlow<List<CaptionItem>> = _captionsList.asStateFlow()

    // Command Bar input
    private val _commandBarQuery = MutableStateFlow("")
    val commandBarQuery: StateFlow<String> = _commandBarQuery.asStateFlow()

    private val _isExecutingCommand = MutableStateFlow(false)
    val isExecutingCommand: StateFlow<Boolean> = _isExecutingCommand.asStateFlow()

    init {
        viewModelScope.launch {
            _releaseInfo.value = GitHubUpdateChecker.checkForUpdates()
        }
    }

    // ==========================================
    // Project Management
    // ==========================================
    fun createNewProject(title: String = "New Project", aspect: AspectRatioType = AspectRatioType.PORTRAIT_9_16) {
        viewModelScope.launch {
            val proj = ProjectRepository.createDefaultProject(title, aspect)
            repository.saveProject(proj)
            loadProject(proj.id)
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun createProjectFromMedia(
        context: Context,
        videoUris: List<Uri>,
        photoUris: List<Uri>,
        audioUris: List<Uri>,
        projectName: String = "Imported Project"
    ) {
        viewModelScope.launch {
            val videoTrackId = UUID.randomUUID().toString()
            val audioTrackId = UUID.randomUUID().toString()
            val textTrackId = UUID.randomUUID().toString()

            val videoClips = mutableListOf<TimelineClip>()
            var currentVideoStartMs = 0L
            var detectedAspect = AspectRatioType.PORTRAIT_9_16

            // Process Videos
            videoUris.forEachIndexed { index, uri ->
                val preparedUri = MediaUtils.prepareMediaUriForEditing(context, uri)
                val info = MediaUtils.queryMediaInfo(context, preparedUri)
                if (index == 0 && info.width > 0 && info.height > 0) {
                    detectedAspect = if (info.width >= info.height) {
                        AspectRatioType.LANDSCAPE_16_9
                    } else {
                        AspectRatioType.PORTRAIT_9_16
                    }
                }

                val clipDur = info.durationMs.coerceAtLeast(1000L)
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = videoTrackId,
                    title = info.title.ifBlank { "Video Clip ${index + 1}" },
                    type = TrackType.VIDEO,
                    startMs = currentVideoStartMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = preparedUri.toString(),
                    previewColorHex = 0xFF7C4DFF
                )
                videoClips.add(clip)
                currentVideoStartMs += clipDur
            }

            // Process Photos
            photoUris.forEachIndexed { index, uri ->
                val preparedUri = MediaUtils.prepareMediaUriForEditing(context, uri)
                val info = MediaUtils.queryMediaInfo(context, preparedUri)
                val clipDur = 3000L
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = videoTrackId,
                    title = info.title.ifBlank { "Photo ${index + 1}" },
                    type = TrackType.VIDEO,
                    startMs = currentVideoStartMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = preparedUri.toString(),
                    previewColorHex = 0xFF00ADB5
                )
                videoClips.add(clip)
                currentVideoStartMs += clipDur
            }

            // Process Audios
            val audioClips = mutableListOf<TimelineClip>()
            var currentAudioStartMs = 0L
            audioUris.forEachIndexed { index, uri ->
                val preparedUri = MediaUtils.prepareMediaUriForEditing(context, uri)
                val info = MediaUtils.queryMediaInfo(context, preparedUri)
                val clipDur = info.durationMs.coerceAtLeast(2000L)
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = audioTrackId,
                    title = info.title.ifBlank { "Audio Track ${index + 1}" },
                    type = TrackType.AUDIO,
                    startMs = currentAudioStartMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = preparedUri.toString(),
                    previewColorHex = 0xFF00E5FF
                )
                audioClips.add(clip)
                currentAudioStartMs += clipDur
            }

            val tracks = listOf(
                TimelineTrack(id = videoTrackId, name = "Video Track", type = TrackType.VIDEO, clips = videoClips),
                TimelineTrack(id = audioTrackId, name = "Audio Track", type = TrackType.AUDIO, clips = audioClips),
                TimelineTrack(id = textTrackId, name = "Text & Captions", type = TrackType.TEXT, clips = emptyList())
            )

            val totalDurationMs = maxOf(currentVideoStartMs, currentAudioStartMs).coerceAtLeast(3000L)
            val resolvedTitle = if (videoClips.isNotEmpty()) {
                videoClips.first().title.substringBeforeLast(".")
            } else if (photoUris.isNotEmpty()) {
                "Photo Story Reel"
            } else {
                projectName
            }

            val newProject = Project(
                id = UUID.randomUUID().toString(),
                title = resolvedTitle,
                aspectRatio = detectedAspect,
                durationMs = totalDurationMs,
                tracks = tracks
            )

            repository.saveProject(newProject)
            loadProject(newProject.id)
            val count = videoClips.size + photoUris.size + audioClips.size
            _uiEvents.emit(StudioUiEvent.ShowToast("Imported $count media file(s)!"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val proj = repository.getProjectById(projectId) ?: allProjects.value.firstOrNull { it.id == projectId }
            if (proj != null) {
                _activeProject.value = proj
                _playheadMs.value = 0L
                _selectedClipId.value = proj.tracks.firstOrNull()?.clips?.firstOrNull()?.id
                _selectedTrackId.value = proj.tracks.firstOrNull()?.id
                undoStack.clear()
                redoStack.clear()
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_activeProject.value?.id == projectId) {
                _activeProject.value = null
            }
            _uiEvents.emit(StudioUiEvent.ShowToast("Project deleted"))
        }
    }

    fun duplicateProject(project: Project) {
        viewModelScope.launch {
            val dup = repository.duplicateProject(project)
            _uiEvents.emit(StudioUiEvent.ShowToast("Duplicated '${dup.title}'"))
        }
    }

    fun toggleFavorite(project: Project) {
        viewModelScope.launch {
            repository.toggleFavorite(project)
        }
    }

    fun loadTemplate(template: TemplateItem) {
        viewModelScope.launch {
            val proj = AIDirectorEngine.createAutoDirectedProject(
                conceptTitle = template.title,
                vibe = when (template.category) {
                    "Cinematic" -> AIDirectorEngine.DirectorVibe.CINEMATIC
                    "Reels" -> AIDirectorEngine.DirectorVibe.FAST_REEL
                    "Travel" -> AIDirectorEngine.DirectorVibe.TRAVEL_VLOG
                    else -> AIDirectorEngine.DirectorVibe.COMMERCIAL
                },
                targetDurationSeconds = template.durationSeconds,
                aspectRatio = template.aspect
            )
            repository.saveProject(proj)
            loadProject(proj.id)
            _uiEvents.emit(StudioUiEvent.ShowToast("Loaded template: ${template.title}"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    // ==========================================
    // Playback & Transport Controls
    // ==========================================
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val currentProject = _activeProject.value ?: return@launch
            val maxDur = currentProject.durationMs
            while (_isPlaying.value) {
                delay(33) // ~30 fps tick
                val next = _playheadMs.value + 33
                if (next >= maxDur) {
                    _playheadMs.value = 0L
                    _isPlaying.value = false
                    break
                } else {
                    _playheadMs.value = next
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(timeMs: Long) {
        val proj = _activeProject.value ?: return
        _playheadMs.value = timeMs.coerceIn(0L, proj.durationMs)
    }

    fun stepFrame(forward: Boolean) {
        val stepMs = 33L
        if (forward) {
            seekTo(_playheadMs.value + stepMs)
        } else {
            seekTo(_playheadMs.value - stepMs)
        }
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.5f)
    }

    fun selectClip(clipId: String?, trackId: String?) {
        _selectedClipId.value = clipId
        if (trackId != null) _selectedTrackId.value = trackId
    }

    fun setAspectRatio(aspect: AspectRatioType) {
        val current = _activeProject.value ?: return
        pushUndo()
        val updated = current.copy(aspectRatio = aspect, updatedAt = System.currentTimeMillis())
        _activeProject.value = updated
        persistCurrentProject()
    }

    // ==========================================
    // Timeline Editing Actions
    // ==========================================
    fun splitClipAtPlayhead() {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val currentPlayhead = _playheadMs.value

        var modified = false
        val newTracks = proj.tracks.map { track ->
            val clipIndex = track.clips.indexOfFirst { it.id == clipId }
            if (clipIndex != -1) {
                val clip = track.clips[clipIndex]
                if (currentPlayhead > clip.startMs && currentPlayhead < (clip.startMs + clip.durationMs)) {
                    pushUndo()
                    val firstDur = currentPlayhead - clip.startMs
                    val secondDur = clip.durationMs - firstDur

                    val firstClip = clip.copy(durationMs = firstDur)
                    val secondClip = clip.copy(
                        id = UUID.randomUUID().toString(),
                        startMs = currentPlayhead,
                        durationMs = secondDur,
                        title = "${clip.title} (Part 2)"
                    )

                    val updatedClips = track.clips.toMutableList()
                    updatedClips[clipIndex] = firstClip
                    updatedClips.add(clipIndex + 1, secondClip)
                    modified = true
                    track.copy(clips = updatedClips)
                } else track
            } else track
        }

        if (modified) {
            _activeProject.value = proj.copy(tracks = newTracks, updatedAt = System.currentTimeMillis())
            persistCurrentProject()
            viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Split clip at playhead")) }
        }
    }

    fun deleteSelectedClip() {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return

        pushUndo()
        val newTracks = proj.tracks.map { track ->
            track.copy(clips = track.clips.filterNot { it.id == clipId })
        }
        _activeProject.value = proj.copy(tracks = newTracks, updatedAt = System.currentTimeMillis())
        _selectedClipId.value = null
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Clip deleted")) }
    }

    fun duplicateSelectedClip() {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return

        pushUndo()
        val newTracks = proj.tracks.map { track ->
            val clip = track.clips.find { it.id == clipId }
            if (clip != null) {
                val dup = clip.copy(
                    id = UUID.randomUUID().toString(),
                    startMs = clip.startMs + clip.durationMs,
                    title = "${clip.title} (Copy)"
                )
                track.copy(clips = track.clips + dup)
            } else track
        }
        _activeProject.value = proj.copy(tracks = newTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Clip duplicated")) }
    }

    fun updateSelectedClipSpeed(speed: Float) {
        updateSelectedClip { it.copy(speed = speed) }
    }

    fun updateSelectedClipVolume(vol: Float) {
        updateSelectedClip { it.copy(volume = vol) }
    }

    fun updateSelectedClipFilter(lutFilter: String) {
        updateSelectedClip { clip ->
            clip.copy(colorGrading = clip.colorGrading.copy(lutFilter = lutFilter))
        }
    }

    fun updateSelectedClipColorSettings(settings: ColorGradingSettings) {
        updateSelectedClip { it.copy(colorGrading = settings) }
    }

    fun updateSelectedClipEffect(effectName: String) {
        updateSelectedClip { it.copy(activeEffect = effectName) }
    }

    fun updateSelectedClipTransition(transitionName: String, durationMs: Long = 500L) {
        updateSelectedClip { it.copy(transitionIn = transitionName, transitionDurationMs = durationMs) }
    }

    fun toggleSelectedClipMute() {
        updateSelectedClip { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSelectedClipBackgroundRemoval() {
        updateSelectedClip { it.copy(aiBackgroundRemoved = !it.aiBackgroundRemoved) }
    }

    fun toggleSelectedClipChromaKey() {
        updateSelectedClip { it.copy(chromaKeyEnabled = !it.chromaKeyEnabled) }
    }

    fun reverseSelectedClip() {
        updateSelectedClip { it.copy(isReversed = !it.isReversed) }
    }

    fun rotateSelectedClip() {
        updateSelectedClip { clip ->
            clip.copy(rotation = (clip.rotation + 90f) % 360f)
        }
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Rotated 90°")) }
    }

    fun flipSelectedClipHorizontal() {
        updateSelectedClip { clip ->
            clip.copy(isFlipHorizontal = !clip.isFlipHorizontal)
        }
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Flipped horizontal")) }
    }

    fun flipSelectedClipVertical() {
        updateSelectedClip { clip ->
            clip.copy(isFlipVertical = !clip.isFlipVertical)
        }
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Flipped vertical")) }
    }

    fun freezeFrameAtPlayhead() {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val currentPlayhead = _playheadMs.value
        pushUndo()
        val newTracks = proj.tracks.map { track ->
            val index = track.clips.indexOfFirst { it.id == clipId }
            if (index != -1) {
                val clip = track.clips[index]
                val freezeClip = clip.copy(
                    id = UUID.randomUUID().toString(),
                    startMs = currentPlayhead,
                    durationMs = 3000L,
                    title = "${clip.title} (Freeze)",
                    isFrozen = true
                )
                val updated = track.clips.toMutableList()
                updated.add(index + 1, freezeClip)
                track.copy(clips = updated)
            } else track
        }
        _activeProject.value = proj.copy(tracks = newTracks, durationMs = proj.durationMs + 3000L, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Freeze frame added (3s)")) }
    }

    fun extractAudioFromSelectedClip() {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val clip = proj.tracks.flatMap { it.clips }.find { it.id == clipId } ?: return
        if (clip.mediaUri == null) {
            viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Selected clip has no audio source")) }
            return
        }
        pushUndo()
        var audioTrack = proj.tracks.find { it.type == TrackType.AUDIO }
        val tracksList = proj.tracks.toMutableList()
        if (audioTrack == null) {
            audioTrack = TimelineTrack(name = "Extracted Audio", type = TrackType.AUDIO)
            tracksList.add(audioTrack)
        }
        val extractedClip = TimelineClip(
            id = UUID.randomUUID().toString(),
            trackId = audioTrack.id,
            title = "Audio: ${clip.title}",
            type = TrackType.AUDIO,
            startMs = clip.startMs,
            durationMs = clip.durationMs,
            sourceDurationMs = clip.sourceDurationMs,
            trimStartMs = clip.trimStartMs,
            trimEndMs = clip.trimEndMs,
            mediaUri = clip.mediaUri,
            previewColorHex = 0xFF00E5FF
        )
        val finalTracks = tracksList.map {
            if (it.id == audioTrack.id) it.copy(clips = it.clips + extractedClip)
            else if (it.clips.any { c -> c.id == clipId }) {
                it.copy(clips = it.clips.map { c -> if (c.id == clipId) c.copy(isMuted = true) else c })
            } else it
        }
        _activeProject.value = proj.copy(tracks = finalTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Extracted audio to dedicated track")) }
    }

    fun trimSelectedClipToPlayhead(trimLeft: Boolean) {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val currentPlayhead = _playheadMs.value

        updateSelectedClip { clip ->
            if (currentPlayhead > clip.startMs && currentPlayhead < (clip.startMs + clip.durationMs)) {
                if (trimLeft) {
                    val delta = currentPlayhead - clip.startMs
                    val newTrimStart = (clip.trimStartMs + (delta * clip.speed).toLong()).coerceIn(0L, clip.sourceDurationMs - 300L)
                    val newDur = clip.durationMs - delta
                    clip.copy(
                        startMs = currentPlayhead,
                        durationMs = newDur.coerceAtLeast(300L),
                        trimStartMs = newTrimStart
                    )
                } else {
                    val newDur = (currentPlayhead - clip.startMs).coerceAtLeast(300L)
                    val newTrimEnd = clip.trimStartMs + (newDur * clip.speed).toLong()
                    clip.copy(
                        durationMs = newDur,
                        trimEndMs = newTrimEnd.coerceAtMost(clip.sourceDurationMs)
                    )
                }
            } else clip
        }
        val label = if (trimLeft) "Trimmed start to playhead" else "Trimmed end to playhead"
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast(label)) }
    }

    fun trimSelectedClipStart(deltaMs: Long) {
        updateSelectedClip { clip ->
            val actualDelta = deltaMs.coerceIn(-clip.startMs, clip.durationMs - 300L)
            val newTrimStart = (clip.trimStartMs + (actualDelta * clip.speed).toLong()).coerceIn(0L, clip.sourceDurationMs - 300L)
            val newDur = (clip.durationMs - actualDelta).coerceAtLeast(300L)
            clip.copy(
                startMs = clip.startMs + actualDelta,
                durationMs = newDur,
                trimStartMs = newTrimStart
            )
        }
    }

    fun trimSelectedClipEnd(deltaMs: Long) {
        updateSelectedClip { clip ->
            val newDur = (clip.durationMs + deltaMs).coerceAtLeast(300L)
            val newTrimEnd = (clip.trimStartMs + (newDur * clip.speed).toLong()).coerceAtMost(clip.sourceDurationMs)
            clip.copy(
                durationMs = newDur,
                trimEndMs = newTrimEnd
            )
        }
    }

    fun resizeSelectedClip(scale: Float) {
        updateSelectedClip { it.copy(scale = scale.coerceIn(0.2f, 4.0f)) }
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Scale set to ${String.format(java.util.Locale.US, "%.1fx", scale)}")) }
    }

    fun replaceSelectedClipAudio(audioUri: Uri, title: String) {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        val clip = proj.tracks.flatMap { it.clips }.find { it.id == clipId } ?: return
        pushUndo()
        var audioTrack = proj.tracks.find { it.type == TrackType.AUDIO }
        val tracksList = proj.tracks.toMutableList()
        if (audioTrack == null) {
            audioTrack = TimelineTrack(name = "Replaced Audio", type = TrackType.AUDIO)
            tracksList.add(audioTrack)
        }
        val newAudioClip = TimelineClip(
            id = UUID.randomUUID().toString(),
            trackId = audioTrack.id,
            title = "Audio: $title",
            type = TrackType.AUDIO,
            startMs = clip.startMs,
            durationMs = clip.durationMs,
            sourceDurationMs = clip.durationMs,
            mediaUri = audioUri.toString(),
            previewColorHex = 0xFF00E5FF
        )
        val finalTracks = tracksList.map {
            if (it.id == audioTrack.id) it.copy(clips = it.clips + newAudioClip)
            else if (it.clips.any { c -> c.id == clipId }) {
                it.copy(clips = it.clips.map { c -> if (c.id == clipId) c.copy(isMuted = true) else c })
            } else it
        }
        _activeProject.value = proj.copy(tracks = finalTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Replaced audio for '${clip.title}'")) }
    }

    fun cropSelectedClip(ratio: String) {
        updateSelectedClip { it.copy(cropRatio = ratio) }
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Crop set to $ratio")) }
    }

    fun reorderSelectedClip(moveLeft: Boolean) {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return
        pushUndo()
        val newTracks = proj.tracks.map { track ->
            val index = track.clips.indexOfFirst { it.id == clipId }
            if (index != -1) {
                val clips = track.clips.toMutableList()
                val targetIndex = if (moveLeft) index - 1 else index + 1
                if (targetIndex in clips.indices) {
                    val clipA = clips[index]
                    val clipB = clips[targetIndex]
                    val startA = clipA.startMs
                    val startB = clipB.startMs
                    clips[index] = clipB.copy(startMs = startA)
                    clips[targetIndex] = clipA.copy(startMs = startB)
                    track.copy(clips = clips)
                } else track
            } else track
        }
        _activeProject.value = proj.copy(tracks = newTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
    }

    fun addMediaToActiveProject(
        context: Context,
        videoUris: List<Uri>,
        photoUris: List<Uri>,
        audioUris: List<Uri>
    ) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            pushUndo()
            var videoTrack = proj.tracks.find { it.type == TrackType.VIDEO }
            var audioTrack = proj.tracks.find { it.type == TrackType.AUDIO }

            val updatedTracks = proj.tracks.toMutableList()
            if (videoTrack == null) {
                videoTrack = TimelineTrack(name = "Video Track", type = TrackType.VIDEO)
                updatedTracks.add(videoTrack)
            }
            if (audioTrack == null) {
                audioTrack = TimelineTrack(name = "Audio Track", type = TrackType.AUDIO)
                updatedTracks.add(audioTrack)
            }

            var videoEndMs = videoTrack.clips.maxOfOrNull { it.startMs + it.durationMs } ?: 0L
            val newVideoClips = videoTrack.clips.toMutableList()

            videoUris.forEach { uri ->
                val info = MediaUtils.queryMediaInfo(context, uri)
                val clipDur = info.durationMs.coerceAtLeast(1000L)
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = videoTrack.id,
                    title = info.title,
                    type = TrackType.VIDEO,
                    startMs = videoEndMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = uri.toString()
                )
                newVideoClips.add(clip)
                videoEndMs += clipDur
            }

            photoUris.forEach { uri ->
                val info = MediaUtils.queryMediaInfo(context, uri)
                val clipDur = 3000L
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = videoTrack.id,
                    title = info.title,
                    type = TrackType.VIDEO,
                    startMs = videoEndMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = uri.toString()
                )
                newVideoClips.add(clip)
                videoEndMs += clipDur
            }

            var audioEndMs = audioTrack.clips.maxOfOrNull { it.startMs + it.durationMs } ?: 0L
            val newAudioClips = audioTrack.clips.toMutableList()
            audioUris.forEach { uri ->
                val info = MediaUtils.queryMediaInfo(context, uri)
                val clipDur = info.durationMs.coerceAtLeast(2000L)
                val clip = TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackId = audioTrack.id,
                    title = info.title,
                    type = TrackType.AUDIO,
                    startMs = audioEndMs,
                    durationMs = clipDur,
                    sourceDurationMs = clipDur,
                    trimStartMs = 0L,
                    trimEndMs = clipDur,
                    mediaUri = uri.toString()
                )
                newAudioClips.add(clip)
                audioEndMs += clipDur
            }

            val finalTracks = updatedTracks.map {
                when (it.id) {
                    videoTrack.id -> it.copy(clips = newVideoClips)
                    audioTrack.id -> it.copy(clips = newAudioClips)
                    else -> it
                }
            }

            val totalDur = maxOf(videoEndMs, audioEndMs).coerceAtLeast(proj.durationMs)
            _activeProject.value = proj.copy(tracks = finalTracks, durationMs = totalDur, updatedAt = System.currentTimeMillis())
            persistCurrentProject()
            _uiEvents.emit(StudioUiEvent.ShowToast("Added media to timeline"))
        }
    }

    fun importMediaAsClip(title: String, uri: Uri, type: TrackType, durationMs: Long) {
        val proj = _activeProject.value ?: return
        pushUndo()
        val track = proj.tracks.find { it.type == type } ?: proj.tracks.first()
        val start = _playheadMs.value
        val newClip = TimelineClip(
            trackId = track.id,
            title = title,
            type = type,
            startMs = start,
            durationMs = durationMs,
            sourceDurationMs = durationMs,
            mediaUri = uri.toString(),
            previewColorHex = when (type) {
                TrackType.VIDEO -> 0xFF7C4DFF
                TrackType.OVERLAY -> 0xFFFFB300
                TrackType.AUDIO -> 0xFF00E5FF
                TrackType.TEXT -> 0xFF2ED573
                TrackType.EFFECT -> 0xFFFF5370
            }
        )
        val updatedTracks = proj.tracks.map {
            if (it.id == track.id) it.copy(clips = it.clips + newClip) else it
        }
        _activeProject.value = proj.copy(tracks = updatedTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch {
            _uiEvents.emit(StudioUiEvent.ShowToast("Added '$title' to project!"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun addTrack(type: TrackType, name: String) {
        val proj = _activeProject.value ?: return
        pushUndo()
        val newTrack = TimelineTrack(name = name, type = type)
        _activeProject.value = proj.copy(tracks = proj.tracks + newTrack, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
    }

    fun addTextOverlay(text: String) {
        val proj = _activeProject.value ?: return
        pushUndo()
        var textTrack = proj.tracks.find { it.type == TrackType.TEXT }
        val tracksList = proj.tracks.toMutableList()

        if (textTrack == null) {
            textTrack = TimelineTrack(name = "Text & Captions", type = TrackType.TEXT)
            tracksList.add(textTrack)
        }

        val newClip = TimelineClip(
            trackId = textTrack.id,
            title = text,
            type = TrackType.TEXT,
            startMs = _playheadMs.value,
            durationMs = 3000L,
            textStyle = TextStyleModel(text = text, animation = "Pop Bounce"),
            previewColorHex = 0xFFFF2E93
        )

        val updatedTracks = tracksList.map {
            if (it.id == textTrack.id) it.copy(clips = it.clips + newClip) else it
        }

        _activeProject.value = proj.copy(tracks = updatedTracks, updatedAt = System.currentTimeMillis())
        _selectedClipId.value = newClip.id
        persistCurrentProject()
        viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Added text layer")) }
    }

    private fun updateSelectedClip(transform: (TimelineClip) -> TimelineClip) {
        val proj = _activeProject.value ?: return
        val clipId = _selectedClipId.value ?: return

        pushUndo()
        val newTracks = proj.tracks.map { track ->
            track.copy(clips = track.clips.map { clip ->
                if (clip.id == clipId) transform(clip) else clip
            })
        }
        _activeProject.value = proj.copy(tracks = newTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
    }

    // ==========================================
    // Undo / Redo
    // ==========================================
    private fun pushUndo() {
        _activeProject.value?.let { undoStack.add(it) }
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            _activeProject.value?.let { redoStack.add(it) }
            _activeProject.value = previous
            persistCurrentProject()
            viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Undo")) }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            _activeProject.value?.let { undoStack.add(it) }
            _activeProject.value = next
            persistCurrentProject()
            viewModelScope.launch { _uiEvents.emit(StudioUiEvent.ShowToast("Redo")) }
        }
    }

    private fun persistCurrentProject() {
        viewModelScope.launch {
            _activeProject.value?.let { repository.saveProject(it) }
        }
    }

    // ==========================================
    // AI Command Bar (Requirement 2)
    // ==========================================
    fun setCommandBarQuery(q: String) {
        _commandBarQuery.value = q
    }

    fun executeAiCommand(command: String) {
        viewModelScope.launch {
            _isExecutingCommand.value = true
            val proj = _activeProject.value ?: repository.allProjects.let {
                ProjectRepository.createDefaultProject("Command Generated Reel")
            }

            when (val actionResult = aiService.parseCommand(command, proj)) {
                is AIResult.Success -> {
                    when (val action = actionResult.data) {
                        is CommandAction.ApplyFilter -> {
                            updateSelectedClipFilter(action.filterName)
                            _uiEvents.emit(StudioUiEvent.ShowToast("Applied ${action.filterName} filter"))
                        }
                        is CommandAction.AddCaptions -> {
                            addTextOverlay("AI Auto Caption (${action.language})")
                            _uiEvents.emit(StudioUiEvent.ShowToast("Added ${action.language} captions"))
                        }
                        is CommandAction.RemoveBackground -> {
                            toggleSelectedClipBackgroundRemoval()
                            _uiEvents.emit(StudioUiEvent.ShowToast("AI background removed"))
                        }
                        is CommandAction.TrimToReel -> {
                            _activeProject.value = proj.copy(durationMs = action.durationMs)
                            persistCurrentProject()
                            _uiEvents.emit(StudioUiEvent.ShowToast("Trimmed project to 30s Instagram Reel"))
                        }
                        is CommandAction.BeatSync -> {
                            updateSelectedClipTransition("Beat Flash", 250L)
                            _uiEvents.emit(StudioUiEvent.ShowToast("Synced cuts to ${action.bpm} BPM beat grid"))
                        }
                        is CommandAction.AddIntro -> {
                            addTextOverlay(action.title)
                            _uiEvents.emit(StudioUiEvent.ShowToast("Created cinematic intro"))
                        }
                        is CommandAction.Unknown -> {
                            addTextOverlay(action.rawCommand)
                            _uiEvents.emit(StudioUiEvent.ShowToast("Processed: ${action.rawCommand}"))
                        }
                    }
                }
                is AIResult.Error -> {
                    _uiEvents.emit(StudioUiEvent.ShowToast(actionResult.message))
                }
                else -> {}
            }
            _commandBarQuery.value = ""
            _isExecutingCommand.value = false
        }
    }

    // ==========================================
    // AI Studio Features (Video, Voice, Music, Director)
    // ==========================================
    fun generateAiStoryboard(prompt: String, durationSec: Int) {
        viewModelScope.launch {
            _aiScriptState.value = AIResult.Loading
            _aiScriptState.value = aiService.generateScriptAndStoryboard(prompt, durationSec)
        }
    }

    fun applyStoryboardToProject(storyboard: AIScriptStoryboard) {
        viewModelScope.launch {
            val proj = AIDirectorEngine.createAutoDirectedProject(
                conceptTitle = storyboard.title,
                vibe = AIDirectorEngine.DirectorVibe.CINEMATIC,
                targetDurationSeconds = storyboard.scenes.sumOf { it.durationSeconds }.coerceAtLeast(12),
                aspectRatio = AspectRatioType.PORTRAIT_9_16,
                rawClipNames = storyboard.scenes.map { "${it.visualDescription}.mp4" }
            )
            repository.saveProject(proj)
            loadProject(proj.id)
            _uiEvents.emit(StudioUiEvent.ShowToast("Created project from storyboard!"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun generateAiVideo(prompt: String, style: String, durationSec: Int, cameraMotion: String) {
        viewModelScope.launch {
            _aiVideoState.value = AIResult.Loading
            _aiVideoState.value = aiService.generateVideo(
                AIVideoGenerationSpec(prompt, style, durationSec, cameraMotion)
            )
        }
    }

    fun importGeneratedVideoToTimeline(video: GeneratedVideoResult) {
        val proj = _activeProject.value ?: return
        pushUndo()
        val videoTrack = proj.tracks.find { it.type == TrackType.VIDEO } ?: proj.tracks.first()
        val newClip = TimelineClip(
            trackId = videoTrack.id,
            title = video.title,
            type = TrackType.VIDEO,
            startMs = _playheadMs.value,
            durationMs = video.durationMs,
            previewColorHex = video.colorHex
        )

        val updatedTracks = proj.tracks.map {
            if (it.id == videoTrack.id) it.copy(clips = it.clips + newClip) else it
        }

        _activeProject.value = proj.copy(tracks = updatedTracks, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch {
            _uiEvents.emit(StudioUiEvent.ShowToast("Imported generated video into timeline"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun generateAiVoice(text: String, voiceName: String, lang: String, speed: Float, pitch: Float) {
        viewModelScope.launch {
            _aiVoiceState.value = AIResult.Loading
            val realResult = ttsEngine.synthesizeSpeechToFile(text, voiceName, lang, speed, pitch)
            realResult.onSuccess { ttsRes ->
                _aiVoiceState.value = AIResult.Success(
                    GeneratedVoiceResult(
                        voiceId = UUID.randomUUID().toString(),
                        text = text,
                        voiceName = voiceName,
                        language = lang,
                        audioDurationMs = ttsRes.durationMs,
                        waveformPoints = List(30) { (Math.sin(it * 0.4).toFloat() * 0.5f + 0.5f).coerceIn(0.1f, 1f) },
                        audioUri = ttsRes.audioUri.toString()
                    )
                )
                _uiEvents.emit(StudioUiEvent.ShowToast("Synthesized audio file (${ttsRes.durationMs / 1000}s)"))
            }.onFailure { err ->
                // Fallback to provider status
                _aiVoiceState.value = aiService.generateVoice(text, voiceName, lang, speed, pitch)
            }
        }
    }

    fun importGeneratedVoiceToTimeline(voice: GeneratedVoiceResult) {
        val proj = _activeProject.value ?: return
        pushUndo()
        var audioTrack = proj.tracks.find { it.type == TrackType.AUDIO }
        val tracksList = proj.tracks.toMutableList()
        if (audioTrack == null) {
            audioTrack = TimelineTrack(name = "Voiceover Track", type = TrackType.AUDIO)
            tracksList.add(audioTrack)
        }

        val newClip = TimelineClip(
            trackId = audioTrack.id,
            title = "Voice: ${voice.voiceName}",
            type = TrackType.AUDIO,
            startMs = _playheadMs.value,
            durationMs = voice.audioDurationMs,
            sourceDurationMs = voice.audioDurationMs,
            trimStartMs = 0L,
            trimEndMs = voice.audioDurationMs,
            mediaUri = voice.audioUri,
            previewColorHex = 0xFF00ADB5
        )

        val updated = tracksList.map {
            if (it.id == audioTrack.id) it.copy(clips = it.clips + newClip) else it
        }

        val maxDur = maxOf(proj.durationMs, _playheadMs.value + voice.audioDurationMs)
        _activeProject.value = proj.copy(tracks = updated, durationMs = maxDur, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch {
            _uiEvents.emit(StudioUiEvent.ShowToast("Imported voiceover audio to project!"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    fun generateAiMusic(prompt: String, genre: String, mood: String, bpm: Int, durationSec: Int) {
        viewModelScope.launch {
            _aiMusicState.value = AIResult.Loading
            try {
                val realWavFile = AudioEngine.synthesizeMusicTrackToFile(
                    context = getApplication<Application>(),
                    genre = genre,
                    mood = mood,
                    bpm = bpm,
                    durationSeconds = durationSec
                )
                val result = GeneratedMusicResult(
                    musicId = UUID.randomUUID().toString(),
                    prompt = prompt,
                    genre = genre,
                    mood = mood,
                    bpm = bpm,
                    durationMs = (durationSec * 1000L),
                    stemAvailable = true,
                    audioUri = Uri.fromFile(realWavFile).toString()
                )
                _aiMusicState.value = AIResult.Success(result)
                _uiEvents.emit(StudioUiEvent.ShowToast("Synthesized music track (${durationSec}s)"))
            } catch (e: Exception) {
                _aiMusicState.value = aiService.generateMusic(prompt, genre, mood, bpm, durationSec)
            }
        }
    }

    fun importGeneratedMusicToTimeline(music: GeneratedMusicResult) {
        val proj = _activeProject.value ?: return
        pushUndo()
        var audioTrack = proj.tracks.find { it.type == TrackType.AUDIO }
        val tracksList = proj.tracks.toMutableList()
        if (audioTrack == null) {
            audioTrack = TimelineTrack(name = "Music BGM", type = TrackType.AUDIO)
            tracksList.add(audioTrack)
        }

        val newClip = TimelineClip(
            trackId = audioTrack.id,
            title = "${music.genre} (${music.bpm} BPM)",
            type = TrackType.AUDIO,
            startMs = _playheadMs.value,
            durationMs = music.durationMs,
            sourceDurationMs = music.durationMs,
            trimStartMs = 0L,
            trimEndMs = music.durationMs,
            mediaUri = music.audioUri,
            previewColorHex = 0xFF11998E
        )

        val updated = tracksList.map {
            if (it.id == audioTrack.id) it.copy(clips = it.clips + newClip) else it
        }

        val maxDur = maxOf(proj.durationMs, _playheadMs.value + music.durationMs)
        _activeProject.value = proj.copy(tracks = updated, durationMs = maxDur, updatedAt = System.currentTimeMillis())
        persistCurrentProject()
        viewModelScope.launch {
            _uiEvents.emit(StudioUiEvent.ShowToast("Imported music into timeline"))
            _uiEvents.emit(StudioUiEvent.NavigateTo("editor"))
        }
    }

    // Auto captions
    fun generateAutoCaptions(contextText: String) {
        viewModelScope.launch {
            val proj = _activeProject.value ?: return@launch
            when (val res = aiService.autoGenerateCaptions(proj.durationMs, contextText)) {
                is AIResult.Success -> {
                    _captionsList.value = res.data
                    _uiEvents.emit(StudioUiEvent.ShowToast("Auto captions generated!"))
                }
                is AIResult.Error -> {
                    _uiEvents.emit(StudioUiEvent.ShowToast(res.message))
                }
                else -> {}
            }
        }
    }

    fun translateCaptions(targetLang: String) {
        viewModelScope.launch {
            when (val res = aiService.translateCaptions(_captionsList.value, targetLang)) {
                is AIResult.Success -> {
                    _captionsList.value = res.data
                    _uiEvents.emit(StudioUiEvent.ShowToast("Captions translated to $targetLang"))
                }
                is AIResult.Error -> {
                    _uiEvents.emit(StudioUiEvent.ShowToast(res.message))
                }
                else -> {}
            }
        }
    }

    // ==========================================
    // Export System
    // ==========================================
    fun startExport(options: ExportOptions) {
        val proj = _activeProject.value ?: return
        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            exportEngine.executeExport(proj, options).collectLatest { state ->
                _exportState.value = state
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _exportState.value = ExportState.Cancelled
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
}
