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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIResult
import com.example.audio.AudioEngine
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AIMusicScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var prompt by remember { mutableStateOf("Upbeat chill lofi beats with warm vinyl crackle and acoustic guitar") }
    var selectedGenre by remember { mutableStateOf(AudioEngine.musicGenres.first()) }
    var selectedMood by remember { mutableStateOf(AudioEngine.musicMoods.first()) }
    var bpm by remember { mutableFloatStateOf(120f) }
    var durationSec by remember { mutableFloatStateOf(30f) }

    val musicState by viewModel.aiMusicState.collectAsState()

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
                    text = "AI Music Studio",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Beat Synthesis • Stem Separation • BGM Generator",
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
                Text(text = "Music Prompt", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe the music track...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }

            item {
                Text(text = "Genre", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(AudioEngine.musicGenres) { genre ->
                        val isSelected = genre == selectedGenre
                        Surface(
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedGenre = genre }
                        ) {
                            Text(
                                text = genre,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(text = "Mood & Vibe", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(AudioEngine.musicMoods) { mood ->
                        val isSelected = mood == selectedMood
                        Surface(
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedMood = mood }
                        ) {
                            Text(
                                text = mood,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tempo (BPM)", color = Color.White, fontSize = 13.sp)
                    Text(text = "${bpm.toInt()} BPM", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = bpm,
                    onValueChange = { bpm = it },
                    valueRange = 70f..175f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.generateAiMusic(prompt, selectedGenre, selectedMood, bpm.toInt(), durationSec.toInt())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Generate Background Track", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // Music State Result
            item {
                when (val st = musicState) {
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
                        val music = st.data
                        Surface(
                            color = Color(0xFF1A1D2B),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Generated AI Music Track", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${music.genre} (${music.bpm} BPM) • ${music.mood}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Button(
                                    onClick = { viewModel.importGeneratedMusicToTimeline(music) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                                ) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(end = 6.dp))
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

            // Sound Effects Pack Section
            item {
                Text(
                    text = "Sound Effects Library (SFX)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(AudioEngine.soundEffectPacks) { sfx ->
                Surface(
                    color = Color(0xFF161926),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* preview / add sfx */ }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF00ADB5))
                            Text(
                                text = sfx,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Text(text = "+ Add", color = Color(0xFF7C4DFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
