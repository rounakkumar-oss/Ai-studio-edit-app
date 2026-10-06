package com.example.ui.tools

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ThumbnailMakerScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var titleText by remember { mutableStateOf("HOW I MADE \$10,000") }
    var subtitleText by remember { mutableStateOf("WITH AI TOOLS (2026)") }
    var selectedAspect by remember { mutableStateOf("16:9 (YouTube)") }
    var selectedBadge by remember { mutableStateOf("🔥 VIRAL") }
    var selectedColorPreset by remember { mutableStateOf(Pair(0xFFFF0055, 0xFF7C4DFF)) }

    val badges = listOf("🔥 VIRAL", "😱 SHOCKING", "✨ NEW AI", "🚀 10X SPEED", "⚡ TUTORIAL", "👑 PRO SECRETS")
    val colorGradients = listOf(
        Pair(0xFFFF0055, 0xFF7C4DFF),
        Pair(0xFF00C0FF, 0xFF42E695),
        Pair(0xFFFF8C00, 0xFFFF0080),
        Pair(0xFF141E30, 0xFF243B55),
        Pair(0xFFFFD200, 0xFFF7971E)
    )

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
                    text = "AI Thumbnail Maker",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "High-CTR Cover Art • YouTube & Reels Presets",
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
            // Live Thumbnail Canvas Preview
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (selectedAspect.startsWith("16:9")) 16f / 9f else 9f / 16f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141724))
                        .border(1.dp, Color(0xFF333B58), RoundedCornerShape(12.dp))
                        .testTag("thumbnail_preview_canvas"),
                    contentAlignment = Alignment.Center
                ) {
                    // Canvas with gradient & geometric art
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.linearGradient(
                                listOf(Color(selectedColorPreset.first), Color(selectedColorPreset.second))
                            )
                        )
                        // Radiant energy burst lines
                        drawLine(
                            color = Color(0x33FFFFFF),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0x33FFFFFF),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, 0f),
                            strokeWidth = 6f
                        )
                    }

                    // Content overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Top Badge
                        Surface(
                            color = Color(0xFF00E5FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = selectedBadge,
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Headline Titles
                        Column {
                            Surface(
                                color = Color(0xDD000000),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = titleText,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = Color(0xDDFFFFFF),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = subtitleText,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Aspect Presets
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("16:9 (YouTube)", "9:16 (Shorts/Reels)").forEach { aspect ->
                        val isSelected = aspect == selectedAspect
                        Surface(
                            color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedAspect = aspect }
                        ) {
                            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                Text(text = aspect, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Text Inputs
            item {
                Text(text = "Main Title (High Impact)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Text(
                    text = "Subtitle / Hook",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = subtitleText,
                    onValueChange = { subtitleText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Badge Chips
            item {
                Text(text = "Engagement Stickers & Badges", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(badges) { b ->
                        val isSelected = b == selectedBadge
                        Surface(
                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E2235),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.clickable { selectedBadge = b }
                        ) {
                            Text(
                                text = b,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Color Presets
            item {
                Text(text = "AI Background Glow", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(colorGradients) { grad ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(Color(grad.first), Color(grad.second))))
                                .border(
                                    width = if (grad == selectedColorPreset) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedColorPreset = grad }
                        )
                    }
                }
            }

            // Export Button
            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                var isExporting by remember { mutableStateOf(false) }
                var exportedUri by remember { mutableStateOf<android.net.Uri?>(null) }

                Column {
                    Button(
                        onClick = {
                            isExporting = true
                            coroutineScope.launch {
                                val res = com.example.util.ThumbnailExporter.renderAndSaveThumbnail(
                                    context = context,
                                    title = titleText,
                                    subtitle = subtitleText,
                                    badge = selectedBadge,
                                    startColor = selectedColorPreset.first,
                                    endColor = selectedColorPreset.second,
                                    is16By9 = selectedAspect.contains("16:9")
                                )
                                isExporting = false
                                res.onSuccess { uri ->
                                    exportedUri = uri
                                    android.widget.Toast.makeText(context, "Thumbnail saved to Pictures/AIStudio!", android.widget.Toast.LENGTH_LONG).show()
                                }.onFailure { err ->
                                    android.widget.Toast.makeText(context, "Export error: ${err.message}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isExporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                    ) {
                        if (isExporting) {
                            androidx.compose.material3.CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Save High-Res 4K Thumbnail to Device", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    if (exportedUri != null) {
                        Button(
                            onClick = {
                                viewModel.importMediaAsClip(
                                    title = "Thumbnail: $titleText",
                                    uri = exportedUri!!,
                                    type = com.example.data.model.TrackType.OVERLAY,
                                    durationMs = 4000L
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                        ) {
                            Text("Add to Video Editor Project", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
