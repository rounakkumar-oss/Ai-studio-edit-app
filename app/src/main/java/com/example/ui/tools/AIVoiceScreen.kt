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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIResult
import com.example.data.model.VoiceCatalog
import com.example.data.model.VoiceProfile
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AIVoiceScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var textToSpeak by remember { mutableStateOf("Welcome to AI Studio. Create professional studio content with voice synthesis.") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedVoice by remember { mutableStateOf(VoiceCatalog.allVoices.first()) }
    var speed by remember { mutableFloatStateOf(1.0f) }
    var pitch by remember { mutableFloatStateOf(1.0f) }

    val voiceState by viewModel.aiVoiceState.collectAsState()

    val filteredVoices = remember(selectedCategory) {
        if (selectedCategory == "All") VoiceCatalog.allVoices
        else VoiceCatalog.allVoices.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

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
                    text = "AI Voice Studio",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "100+ Realistic Voices • Hindi, English & Regional",
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
                Text(text = "Script / Voiceover Text", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = textToSpeak,
                    onValueChange = { textToSpeak = it },
                    placeholder = { Text("Enter script for text-to-speech...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            item {
                Text(text = "Voice Categories (100+ Styles)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(VoiceCatalog.voiceCategories) { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Voice Selector Row
            item {
                Text(text = "Select Voice Persona", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(filteredVoices) { voice ->
                        val isSelected = voice.id == selectedVoice.id
                        Surface(
                            color = if (isSelected) Color(0xFF2E1A47) else Color(0xFF161926),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clickable { selectedVoice = voice }
                                .padding(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = voice.name,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${voice.gender} • ${voice.language}",
                                    color = Color(0xFFA0A5B5),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = voice.accentTag,
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Speed & Pitch Sliders
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Speech Speed", color = Color.White, fontSize = 13.sp)
                    Text(text = "${String.format("%.2f", speed)}x", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Voice Pitch", color = Color.White, fontSize = 13.sp)
                    Text(text = "${String.format("%.2f", pitch)}x", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = pitch,
                    onValueChange = { pitch = it },
                    valueRange = 0.6f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.generateAiVoice(
                            text = textToSpeak,
                            voiceName = selectedVoice.name,
                            lang = selectedVoice.language,
                            speed = speed,
                            pitch = pitch
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Synthesize Voiceover Audio", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // State result
            item {
                when (val st = voiceState) {
                    is AIResult.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF161926)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF7C4DFF))
                        }
                    }

                    is AIResult.Success -> {
                        val voice = st.data
                        Surface(
                            color = Color(0xFF1A1D2B),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Synthesized Voiceover", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${voice.voiceName} (${voice.language}) • ${voice.audioDurationMs / 1000}s",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Button(
                                    onClick = { viewModel.importGeneratedVoiceToTimeline(voice) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                                ) {
                                    Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(end = 6.dp))
                                    Text("Add to Project Audio Track", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    is AIResult.Error -> {
                        Surface(
                            color = Color(0xFF2D1822),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = st.message, color = Color(0xFFFF5252), fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                        }
                    }

                    null -> {}
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
