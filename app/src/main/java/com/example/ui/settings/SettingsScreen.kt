package com.example.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.updater.GitHubUpdateChecker
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SettingsScreen(
    viewModel: StudioViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current

    val releaseInfo by viewModel.releaseInfo.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()

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
            Text(
                text = "Studio Settings & Distribution",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI Provider Status (Requirement 37)
            item {
                Text(text = "AI Engine Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                val isCfg = viewModel.aiService.isConfigured
                Surface(
                    color = Color(0xFF161926),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = viewModel.aiService.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Surface(
                                color = if (isCfg) Color(0xFF00ADB5) else Color(0xFFFF5252),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isCfg) "ACTIVE" else "NOT CONFIGURED",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = viewModel.aiService.statusMessage,
                            color = Color(0xFFA0A5B5),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            // 2. GitHub Distribution & Direct APK Download (Requirements 26, 27, 28, 31, 32)
            item {
                Text(text = "App Distribution & GitHub Releases", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = Color(0xFF161926),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Current Version", color = Color(0xFFA0A5B5), fontSize = 13.sp)
                            Text(text = GitHubUpdateChecker.CURRENT_VERSION, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Normal User Workflow: Download the prebuilt, signed APK directly from GitHub Releases and tap to install — no Android Studio, Gradle, or developer tools required.",
                            color = Color(0xCCFFFFFF),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    GitHubUpdateChecker.openBrowserUrl(context, GitHubUpdateChecker.LATEST_RELEASE_PAGE_URL)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                                Text("Download APK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    GitHubUpdateChecker.openBrowserUrl(context, GitHubUpdateChecker.GITHUB_REPO_URL)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                                Text("Source ZIP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Performance & Low-RAM Optimization (Requirement 24)
            item {
                Text(text = "Hardware & Memory Optimization", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = Color(0xFF161926),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Proxy Media Preview", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Uses lightweight proxies to prevent lag on low RAM devices", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                            }
                            Switch(
                                checked = userPreferences.proxyMediaPreview,
                                onCheckedChange = { viewModel.setProxyMediaPreview(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF7C4DFF))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Hardware Accelerated Pipeline", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "GPU shaders for real-time 60 FPS effects", color = Color(0xFFA0A5B5), fontSize = 11.sp)
                            }
                            Switch(
                                checked = userPreferences.hardwareAcceleration,
                                onCheckedChange = { viewModel.setHardwareAcceleration(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF7C4DFF))
                            )
                        }
                    }
                }
            }

            // 4. Privacy & Data Security (Requirement 33 & 34)
            item {
                Text(text = "Privacy & Safe File Processing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(
                    color = Color(0xFF161926),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00ADB5))
                            Text(
                                text = "Zero Data Leakage Guarantee",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Text(
                            text = "• All project files, audio, and video cuts remain stored locally on your device in the Room database.\n• No video footage is ever uploaded to external servers without explicit user confirmation.\n• Standard zero-permission photo picker protects all private gallery media.",
                            color = Color(0xFFA0A5B5),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
