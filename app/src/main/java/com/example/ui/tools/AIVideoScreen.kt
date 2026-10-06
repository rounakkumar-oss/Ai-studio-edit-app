package com.example.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ai.GeneratedVideoResult
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AIVideoScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var prompt by remember { mutableStateOf("Neon cyberpunk drone flight through futuristic skyscraper canyon with rain") }
    var selectedStyle by remember { mutableStateOf("Cinematic") }
    var cameraMotion by remember { mutableStateOf("Pan Right") }
    var durationSec by remember { mutableIntStateOf(4) }

    val styles = listOf("Cinematic", "3D Anime", "Cyberpunk", "Hyper-realistic", "Vintage Film", "Retro 90s")
    val cameraMotions = listOf("Pan Right", "Pan Left", "Zoom In", "Orbit 360", "Dolly Forward", "Crane Up")

    val videoState by viewModel.aiVideoState.collectAsState()

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
                    text = "AI Video Studio",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Text-to-Video & Image-to-Video Generation",
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
                Text(text = "Prompt", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Describe the video scene...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            item {
                Text(text = "Visual Style", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(styles) { style ->
                        val isSelected = style == selectedStyle
                        Surface(
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedStyle = style }
                        ) {
                            Text(
                                text = style,
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
                Text(text = "Camera Motion", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(cameraMotions) { motion ->
                        val isSelected = motion == cameraMotion
                        Surface(
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { cameraMotion = motion }
                        ) {
                            Text(
                                text = motion,
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
                Button(
                    onClick = {
                        viewModel.generateAiVideo(prompt, selectedStyle, durationSec, cameraMotion)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Generate Video Clip", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // Generation State Card
            item {
                when (val state = videoState) {
                    is AIResult.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF161926)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF7C4DFF))
                                Text(
                                    text = "Synthesizing AI Video Frames...",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }

                    is AIResult.Success -> {
                        val video = state.data
                        Surface(
                            color = Color(0xFF1A1D2B),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Generated Video", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = video.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                                Text(text = "Duration: ${video.durationMs / 1000}s • Prompt: ${video.prompt}", color = Color(0xFFA0A5B5), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

                                Button(
                                    onClick = { viewModel.importGeneratedVideoToTimeline(video) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                                ) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(end = 6.dp))
                                    Text("Add to Project Timeline", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    is AIResult.Error -> {
                        Surface(
                            color = Color(0xFF221727),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A254B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (state.isNotConfigured) "⚠️ AI Service Not Configured" else "Generation Error",
                                    color = Color(0xFFFF7675),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.message,
                                    color = Color(0xFFDFE6E9),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                Text(
                                    text = "To enable cloud AI generation:\n1. Open Google AI Studio\n2. Open Secrets Panel\n3. Add 'GEMINI_API_KEY' with your Google Gemini API key\n4. Or enter key in App Settings",
                                    color = Color(0xFFA0A5B5),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
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
