package com.example.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIDirectorEngine
import com.example.ai.AIResult
import com.example.data.model.AspectRatioType
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AIDirectorScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var promptText by remember { mutableStateOf("Make a 30-second Jharkhand travel video with scenic hills and waterfalls") }
    var selectedVibe by remember { mutableStateOf(AIDirectorEngine.DirectorVibe.TRAVEL_VLOG) }
    var durationSec by remember { mutableFloatStateOf(30f) }
    var selectedAspect by remember { mutableStateOf(AspectRatioType.PORTRAIT_9_16) }

    val scriptState by viewModel.aiScriptState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable { onNavigateBack() }
            )
            Column {
                Text(
                    text = "AI Director",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Idea → Script → Storyboard → Auto-Cut Timeline",
                    color = Color(0xFFA0A5B5),
                    fontSize = 12.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF)))
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Text(
                                text = "Intelligent Auto-Editing",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Text(
                            text = "AI analyzes your idea, scripts scenes, detects beats, adds transitions, and generates an editable timeline project.",
                            color = Color(0xEEFFFFFF),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            item {
                Text(text = "Project Concept & Prompt", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    placeholder = { Text("e.g., 'Make a 30-second Jharkhand travel video'") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            item {
                Text(text = "Directorial Style & Vibe", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(AIDirectorEngine.DirectorVibe.values()) { vibe ->
                        val isSelected = vibe == selectedVibe
                        Surface(
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedVibe = vibe }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(
                                    text = vibe.label,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${vibe.bpm} BPM • ${vibe.filterName}",
                                    color = if (isSelected) Color(0xCCFFFFFF) else Color(0xFFA0A5B5),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Target Duration", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(text = "${durationSec.toInt()}s", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = durationSec,
                    onValueChange = { durationSec = it },
                    valueRange = 10f..60f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
                )
            }

            item {
                // Generate button
                Button(
                    onClick = {
                        val proj = AIDirectorEngine.createAutoDirectedProject(
                            conceptTitle = promptText.take(28),
                            vibe = selectedVibe,
                            targetDurationSeconds = durationSec.toInt(),
                            aspectRatio = selectedAspect
                        )
                        viewModel.loadTemplate(
                            com.example.data.model.TemplateItem(
                                id = "directed_${System.currentTimeMillis()}",
                                title = promptText.take(28),
                                category = selectedVibe.name,
                                durationSeconds = durationSec.toInt(),
                                aspect = selectedAspect,
                                description = promptText,
                                musicGenre = selectedVibe.label,
                                tags = listOf("AI Directed"),
                                colorGradient = Pair(0xFF7C4DFF, 0xFF00E5FF)
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Auto-Direct & Build Timeline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
