package com.example.ui.export

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.data.model.Project
import com.example.video.ExportOptions
import com.example.video.ExportState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSheet(
    project: Project,
    exportState: ExportState,
    onStartExport: (ExportOptions) -> Unit,
    onCancelExport: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableIntStateOf(30) }

    val resolutions = listOf("720p", "1080p", "1440p", "4K UHD")
    val fpsOptions = listOf(24, 30, 60)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141724)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Export Project",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            when (exportState) {
                is ExportState.Idle -> {
                    // Resolution selector
                    Text(
                        text = "Resolution",
                        color = Color(0xFFA0A5B5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        resolutions.forEach { res ->
                            val isSelected = res == selectedResolution
                            Surface(
                                color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF222638),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedResolution = res }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = res,
                                        color = if (isSelected) Color.White else Color(0xFFA0A5B5),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Frame rate selector
                    Text(
                        text = "Frame Rate (FPS)",
                        color = Color(0xFFA0A5B5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fpsOptions.forEach { fps ->
                            val isSelected = fps == selectedFps
                            Surface(
                                color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF222638),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFps = fps }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${fps} fps",
                                        color = if (isSelected) Color.White else Color(0xFFA0A5B5),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Metadata preview summary
                    Surface(
                        color = Color(0xFF1E2235),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Format:", color = Color(0xFFA0A5B5), fontSize = 12.sp)
                                Text("MP4 (H.264 / AAC)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Estimated Size:", color = Color(0xFFA0A5B5), fontSize = 12.sp)
                                val estMb = (project.durationMs / 1000f) * 1.5f
                                Text("~${String.format("%.1f", estMb)} MB", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val (w, h) = when (selectedResolution) {
                                "720p" -> Pair(720, 1280)
                                "1080p" -> Pair(1080, 1920)
                                "1440p" -> Pair(1440, 2560)
                                else -> Pair(2160, 3840)
                            }
                            onStartExport(
                                ExportOptions(
                                    resolutionLabel = selectedResolution,
                                    width = w,
                                    height = h,
                                    fps = selectedFps,
                                    bitrateMbps = if (selectedResolution == "4K UHD") 35f else 14f
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Start Exporting Video", fontWeight = FontWeight.Bold)
                    }
                }

                is ExportState.Progress -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rendering Frames...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Frame ${exportState.currentFrame} / ${exportState.totalFrames} • ETA: ${exportState.etaSeconds}s",
                            color = Color(0xFFA0A5B5),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        LinearProgressIndicator(
                            progress = exportState.percent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF7C4DFF),
                            trackColor = Color(0xFF222638)
                        )

                        Text(
                            text = "${(exportState.percent * 100).toInt()}%",
                            color = Color(0xFF00E5FF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        OutlinedButton(
                            onClick = onCancelExport,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Text("Cancel Export", color = Color(0xFFFF5252))
                        }
                    }
                }

                is ExportState.Finished -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Export Completed!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = "File saved (${String.format("%.1f", exportState.fileSizeMb)} MB)",
                            color = Color(0xFFA0A5B5),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Surface(
                            color = Color(0xFF1E2235),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp)
                        ) {
                            Text(
                                text = exportState.outputPath,
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is ExportState.Error -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                        Text(text = "Export Error", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = exportState.message, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                        Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Dismiss")
                        }
                    }
                }

                ExportState.Cancelled -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                        Text(text = "Export Cancelled", color = Color(0xFFA0A5B5), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Button(onClick = onDismiss, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
