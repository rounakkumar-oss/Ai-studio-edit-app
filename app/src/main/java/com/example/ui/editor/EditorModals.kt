package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ColorGradingSettings
import com.example.data.model.EffectsCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectsGalleryModal(
    onDismiss: () -> Unit,
    onSelectEffect: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredEffects = remember(selectedCategory, searchQuery) {
        EffectsCatalog.allEffects.filter { effect ->
            (selectedCategory == "All" || effect.category.equals(selectedCategory, ignoreCase = true)) &&
                    (searchQuery.isBlank() || effect.name.contains(searchQuery, ignoreCase = true))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "100+ Effects Library",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 100+ cinematic, glitch, AI effects...", color = Color(0xFFA0A5B5), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFA0A5B5)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                items(EffectsCatalog.categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF222638),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else Color(0xFFA0A5B5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Effects Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .padding(top = 8.dp)
            ) {
                items(filteredEffects) { effect ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E2235))
                            .border(1.dp, Color(0xFF2E344D), RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectEffect(effect.name)
                                onDismiss()
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(effect.accentColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "FX",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = effect.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Text(
                            text = effect.category,
                            color = Color(0xFFA0A5B5),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransitionsModal(
    onDismiss: () -> Unit,
    onSelectTransition: (String, Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var durationMs by remember { mutableFloatStateOf(500f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cinematic Transitions",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Duration Slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Transition Duration", color = Color(0xFFA0A5B5), fontSize = 13.sp)
                Text(text = "${(durationMs / 1000f)}s", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
            }
            Slider(
                value = durationMs,
                onValueChange = { durationMs = it },
                valueRange = 200f..1500f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00E5FF),
                    activeTrackColor = Color(0xFF00E5FF)
                )
            )

            // Transitions List
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(top = 8.dp)
            ) {
                items(EffectsCatalog.allTransitions) { tr ->
                    Surface(
                        color = Color(0xFF1E2235),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectTransition(tr.name, durationMs.toLong())
                                onDismiss()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "⚡", fontSize = 18.sp)
                            Text(
                                text = tr.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(text = tr.category, color = Color(0xFFA0A5B5), fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorGradingModal(
    currentSettings: ColorGradingSettings,
    onDismiss: () -> Unit,
    onApplySettings: (ColorGradingSettings) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var brightness by remember { mutableFloatStateOf(currentSettings.brightness) }
    var contrast by remember { mutableFloatStateOf(currentSettings.contrast) }
    var saturation by remember { mutableFloatStateOf(currentSettings.saturation) }
    var vignette by remember { mutableFloatStateOf(currentSettings.vignette) }
    var selectedLut by remember { mutableStateOf(currentSettings.lutFilter) }

    val lutPresets = listOf("Normal", "Teal & Orange", "Cyberpunk", "Film Noir", "Warm Sunset", "Retro VHS", "Pastel Dream")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Color Grading & LUT Filters",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // LUT Preset Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(lutPresets) { lut ->
                    val isSelected = lut == selectedLut
                    Surface(
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF222638),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { selectedLut = lut }
                    ) {
                        Text(
                            text = lut,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Sliders
            SliderControlItem("Brightness", brightness, -0.5f..0.5f) { brightness = it }
            SliderControlItem("Contrast", contrast, 0.5f..1.8f) { contrast = it }
            SliderControlItem("Saturation", saturation, 0.0f..2.0f) { saturation = it }
            SliderControlItem("Vignette", vignette, 0.0f..1.0f) { vignette = it }

            Button(
                onClick = {
                    onApplySettings(
                        ColorGradingSettings(
                            brightness = brightness,
                            contrast = contrast,
                            saturation = saturation,
                            vignette = vignette,
                            lutFilter = selectedLut
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
            ) {
                Text("Apply Color Grade", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SliderControlItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFA0A5B5), fontSize = 12.sp)
        Text(text = String.format("%.2f", value), color = Color.White, fontSize = 12.sp)
    }
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = range,
        colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedModal(
    currentSpeed: Float,
    onDismiss: () -> Unit,
    onApplySpeed: (Float) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var speed by remember { mutableFloatStateOf(currentSpeed) }
    val presets = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Playback Speed", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${String.format(java.util.Locale.US, "%.2f", speed)}x", color = Color(0xFF00E5FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(presets) { p ->
                    val isSelected = (speed - p).let { it > -0.05f && it < 0.05f }
                    Surface(
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF222638),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { speed = p }
                    ) {
                        Text(
                            text = "${p}x",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0.2f..3.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF7C4DFF), activeTrackColor = Color(0xFF7C4DFF))
            )

            Button(
                onClick = {
                    onApplySpeed(speed)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
            ) {
                Text("Apply Speed", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeModal(
    currentVolume: Float,
    isMuted: Boolean,
    onDismiss: () -> Unit,
    onApplyVolume: (Float) -> Unit,
    onToggleMute: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var volume by remember { mutableFloatStateOf(currentVolume) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Clip Volume", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = if (isMuted) "MUTED" else "${(volume * 100).toInt()}%", color = if (isMuted) Color(0xFFFF5252) else Color(0xFF00E5FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Slider(
                value = volume,
                onValueChange = { volume = it },
                valueRange = 0.0f..2.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF00ADB5), activeTrackColor = Color(0xFF00ADB5)),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onToggleMute,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isMuted) Color(0xFF2ED573) else Color(0xFFFF5252))
                ) {
                    Text(if (isMuted) "Unmute" else "Mute Clip", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onApplyVolume(volume)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Apply Volume", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropAspectModal(
    onDismiss: () -> Unit,
    onSelectRatio: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val ratios = listOf("Original", "9:16 (Reels/TikTok)", "16:9 (YouTube)", "1:1 (Square)", "4:5 (Instagram)", "21:9 (Cinematic)")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(text = "Crop & Aspect Ratio", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            ratios.forEach { r ->
                Surface(
                    color = Color(0xFF1E2235),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onSelectRatio(r.substringBefore(" "))
                            onDismiss()
                        }
                ) {
                    Text(text = r, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioToolsModal(
    onDismiss: () -> Unit,
    onAddMusic: () -> Unit,
    onAddVoiceover: () -> Unit,
    onExtractAudio: () -> Unit,
    onOpenAiMusic: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Audio & Sound Tools", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Surface(
                color = Color(0xFF1E2235),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onDismiss(); onAddMusic() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎵", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text("Add Music from Phone", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Import MP3/WAV tracks from storage", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                    }
                }
            }

            Surface(
                color = Color(0xFF1E2235),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onDismiss(); onOpenAiMusic() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text("AI Music Generator", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Generate beat-matched BGM and lo-fi tracks", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                    }
                }
            }

            Surface(
                color = Color(0xFF1E2235),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onDismiss(); onAddVoiceover() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🎙️", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text("AI Voice-Over (TTS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Type script in Hindi/English for natural voice", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                    }
                }
            }

            Surface(
                color = Color(0xFF1E2235),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onDismiss(); onExtractAudio() }
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✂️", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                    Column {
                        Text("Extract Audio from Video", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Separate video soundtrack to editable track", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsModal(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "AI Studio Creation Tools", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            val tools = listOf(
                Triple("ai_video", "AI Video Generator", "Text-to-Video & prompt scenes"),
                Triple("ai_voice", "AI Voice & Text-to-Speech", "100+ voices, Hindi & multi-lingual"),
                Triple("ai_music", "AI Music Generator", "Beat synthesis & soundtrack creation"),
                Triple("captions", "Auto Captions & Subtitles", "Speech recognition & transcript burn-in"),
                Triple("ai_director", "AI Director & Storyboard", "Auto script-to-video arrangement")
            )

            tools.forEach { (route, title, desc) ->
                Surface(
                    color = Color(0xFF1E2235),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onDismiss(); onNavigate(route) }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                        Column {
                            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(desc, color = Color(0xFFA0A5B5), fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

