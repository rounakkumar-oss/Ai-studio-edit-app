package com.example

import com.example.ai.AIDirectorEngine
import com.example.data.model.AspectRatioType
import com.example.data.model.EffectsCatalog
import com.example.data.model.VoiceCatalog
import com.example.data.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun effectsCatalog_hasOver100Effects() {
        assertTrue(
            "Effects library must have at least 100 effects, found: ${EffectsCatalog.allEffects.size}",
            EffectsCatalog.allEffects.size >= 100
        )
    }

    @Test
    fun voiceCatalog_hasOver100Voices() {
        assertTrue(
            "Voice library must have at least 100 voices, found: ${VoiceCatalog.allVoices.size}",
            VoiceCatalog.allVoices.size >= 100
        )
    }

    @Test
    fun aiDirector_createsMultiLayerEditableProject() {
        val project = AIDirectorEngine.createAutoDirectedProject(
            conceptTitle = "Jharkhand Travel Reel",
            vibe = AIDirectorEngine.DirectorVibe.TRAVEL_VLOG,
            targetDurationSeconds = 30,
            aspectRatio = AspectRatioType.PORTRAIT_9_16
        )
        assertEquals("Jharkhand Travel Reel", project.title)
        assertEquals(30000L, project.durationMs)
        assertTrue(project.tracks.size >= 3)
    }

    @Test
    fun defaultProject_hasMultipleTracks() {
        val project = ProjectRepository.createDefaultProject("Cyberpunk City")
        assertTrue(project.tracks.isNotEmpty())
    }

    @Test
    fun userPreferences_defaultStateValid() {
        val prefs = com.example.data.repository.UserPreferences()
        assertTrue(prefs.proxyMediaPreview)
        assertTrue(prefs.hardwareAcceleration)
        assertEquals("1080p", prefs.defaultResolution)
        assertEquals(30, prefs.defaultFps)
    }
}
