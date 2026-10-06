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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AutoCaptionsScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var contextText by remember { mutableStateOf("Travel vlog exploring the waterfalls and lush hills of Netarhat, Jharkhand.") }
    val captions by viewModel.captionsList.collectAsState()

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
                    text = "Auto Captions & Subtitles",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Speech-to-Text • Karaoke Highlight • SRT Export",
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
                Text(text = "Video Audio Context / Topic", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = contextText,
                    onValueChange = { contextText = it },
                    placeholder = { Text("Topic context for high accuracy speech recognition...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateAutoCaptions(contextText) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text("Auto Transcribe", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.translateCaptions("Hindi") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ADB5))
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text("Translate (Hindi)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Captions List
            if (captions.isNotEmpty()) {
                item {
                    Text(
                        text = "Generated Timed Subtitles (${captions.size} segments)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(captions) { item ->
                    Surface(
                        color = Color(0xFF161926),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = String.format("%02d:%02d - %02d:%02d", item.startMs / 1000 / 60, item.startMs / 1000 % 60, item.endMs / 1000 / 60, item.endMs / 1000 % 60),
                                    color = Color(0xFF00E5FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "Confidence: ${(item.confidence * 100).toInt()}%", color = Color(0xFFA0A5B5), fontSize = 10.sp)
                            }
                            Text(
                                text = item.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (item.translatedText != null) {
                                Text(
                                    text = item.translatedText,
                                    color = Color(0xFFFFD700),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            // Apply to timeline as text overlay tracks
                            captions.forEach { cap ->
                                viewModel.addTextOverlay(cap.translatedText ?: cap.text)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                    ) {
                        Text("Burn Captions to Video Timeline", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
