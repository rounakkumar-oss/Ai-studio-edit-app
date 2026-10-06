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
