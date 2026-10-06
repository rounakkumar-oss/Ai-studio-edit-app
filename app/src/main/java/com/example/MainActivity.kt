package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.editor.VideoEditorScreen
import com.example.ui.home.HomeScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.tools.AIDirectorScreen
import com.example.ui.tools.AIMusicScreen
import com.example.ui.tools.AIVideoScreen
import com.example.ui.tools.AIVoiceScreen
import com.example.ui.tools.AutoCaptionsScreen
import com.example.ui.tools.ThumbnailMakerScreen
import com.example.ui.viewmodel.StudioUiEvent
import com.example.ui.viewmodel.StudioViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090A0F)
                ) {
                    AIStudioAppRoot()
                }
            }
        }
    }
}

@Composable
fun AIStudioAppRoot(viewModel: StudioViewModel = viewModel()) {
    val navStack = remember { mutableStateListOf("home") }
    val currentScreen = navStack.lastOrNull() ?: "home"

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is StudioUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is StudioUiEvent.NavigateTo -> {
                    if (navStack.lastOrNull() != event.screenRoute) {
                        navStack.add(event.screenRoute)
                    }
                }
                is StudioUiEvent.OpenExportSheet -> {
                    // Handled within editor screen
                }
            }
        }
    }

    val popBack: () -> Unit = {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.lastIndex)
        }
    }

    val navigateTo: (String) -> Unit = { route ->
        navStack.add(route)
    }

    when (currentScreen) {
        "home" -> HomeScreen(
            viewModel = viewModel,
            onNavigate = navigateTo
        )
        "editor" -> VideoEditorScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "ai_video" -> AIVideoScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "ai_voice" -> AIVoiceScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "ai_music" -> AIMusicScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "ai_director" -> AIDirectorScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "thumbnail_maker" -> ThumbnailMakerScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "captions" -> AutoCaptionsScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        "settings" -> SettingsScreen(
            viewModel = viewModel,
            onNavigateBack = popBack
        )
        else -> HomeScreen(
            viewModel = viewModel,
            onNavigate = navigateTo
        )
    }
}
