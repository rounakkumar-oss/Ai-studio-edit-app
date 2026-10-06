package com.example.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoSizeSelectActual
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.model.TemplateItem
import com.example.data.model.TemplatesCatalog
import com.example.ui.viewmodel.StudioViewModel

data class StudioQuickTool(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }

    val pickVideosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.createProjectFromMedia(
                context = context,
                videoUris = uris,
                photoUris = emptyList(),
                audioUris = emptyList(),
                projectName = "Video Project"
            )
            onNavigate("editor")
        }
    }

    val pickSingleVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.createProjectFromMedia(
                context = context,
                videoUris = listOf(uri),
                photoUris = emptyList(),
                audioUris = emptyList(),
                projectName = "Video Project"
            )
            onNavigate("editor")
        }
    }

    val pickPhotosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.createProjectFromMedia(
                context = context,
                videoUris = emptyList(),
                photoUris = uris,
                audioUris = emptyList(),
                projectName = "Photo Story"
            )
            onNavigate("editor")
        }
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.createProjectFromMedia(
                context = context,
                videoUris = emptyList(),
                photoUris = emptyList(),
                audioUris = uris,
                projectName = "Audio Project"
            )
            onNavigate("editor")
        }
    }

    val pickSingleAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.createProjectFromMedia(
                context = context,
                videoUris = emptyList(),
                photoUris = emptyList(),
                audioUris = listOf(uri),
                projectName = "Audio Project"
            )
            onNavigate("editor")
        }
    }

    val allProjects by viewModel.allProjects.collectAsState()
    val commandQuery by viewModel.commandBarQuery.collectAsState()
    val isExecutingCommand by viewModel.isExecutingCommand.collectAsState()

    val quickCommands = listOf(
        "Make this video cinematic",
        "Remove background",
        "Add Hindi captions",
        "Make a 30-second Instagram Reel",
        "Add beat-synced transitions",
        "Create a cinematic intro"
    )

    val studioTools = listOf(
        StudioQuickTool("AI Video", "Text-to-Video", Icons.Default.Videocam, "ai_video", Color(0xFF7C4DFF)),
        StudioQuickTool("AI Voice", "100+ TTS Styles", Icons.Default.Mic, "ai_voice", Color(0xFF00ADB5)),
        StudioQuickTool("AI Music", "BGM & Beats", Icons.Default.Audiotrack, "ai_music", Color(0xFF00E5FF)),
        StudioQuickTool("AI Director", "Auto-Edit Script", Icons.Default.Movie, "ai_director", Color(0xFFFF5370)),
        StudioQuickTool("Thumbnail", "YouTube / Reels", Icons.Default.PhotoSizeSelectActual, "thumbnail_maker", Color(0xFFFFB300)),
        StudioQuickTool("Auto Captions", "Speech-to-Text", Icons.Default.ClosedCaption, "captions", Color(0xFF2ED573))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7C4DFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "AI Studio",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            IconButton(
                onClick = { onNavigate("settings") },
                modifier = Modifier.testTag("home_settings_button")
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFFA0A5B5))
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI Command Bar (Requirement 2)
            item {
                Surface(
                    color = Color(0xFF141724),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "AI Command Bar",
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commandQuery,
                                onValueChange = { viewModel.setCommandBarQuery(it) },
                                placeholder = {
                                    Text(
                                        "Ask AI (e.g., 'Make this video cinematic')...",
                                        color = Color(0xFF6B728E),
                                        fontSize = 12.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF7C4DFF),
                                    unfocusedBorderColor = Color(0xFF282D42),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_command_input")
                            )

                            IconButton(
                                onClick = {
                                    if (commandQuery.isNotBlank()) {
                                        viewModel.executeAiCommand(commandQuery)
                                    }
                                },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7C4DFF))
                                    .testTag("ai_command_send_button")
                            ) {
                                if (isExecutingCommand) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Run Command",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Quick command suggestion chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            items(quickCommands) { cmd ->
                                Surface(
                                    color = Color(0xFF1E2235),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable {
                                        viewModel.setCommandBarQuery(cmd)
                                        viewModel.executeAiCommand(cmd)
                                    }
                                ) {
                                    Text(
                                        text = cmd,
                                        color = Color(0xFFA0A5B5),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. New Project Hero Button & Media Selectors
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF2575FC)))
                            )
                            .clickable {
                                showImportDialog = true
                            }
                            .testTag("new_project_hero_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(text = "NEW PROJECT", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                Text(text = "Import video, photo or audio from phone", color = Color(0xEEFFFFFF), fontSize = 12.sp)
                            }
                        }
                    }

                    // Direct Quick Media Selectors (Requirement: Select Video, Select Photos, Select Audio)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Select Video
                        Surface(
                            color = Color(0xFF161A29),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    pickVideosLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                }
                                .testTag("select_video_quick_button")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(24.dp))
                                Text("Select Video", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                            }
                        }

                        // Select Photos
                        Surface(
                            color = Color(0xFF161A29),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    pickPhotosLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("select_photos_quick_button")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF00ADB5), modifier = Modifier.size(24.dp))
                                Text("Select Photos", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                            }
                        }

                        // Select Audio
                        Surface(
                            color = Color(0xFF161A29),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    pickAudioLauncher.launch(arrayOf("audio/*"))
                                }
                                .testTag("select_audio_quick_button")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                                Text("Select Audio", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }

            // 3. AI Studio Tools Grid
            item {
                Text(
                    text = "AI Creation Tools",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(studioTools) { tool ->
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF141724))
                                .border(1.dp, Color(0xFF222638), RoundedCornerShape(14.dp))
                                .clickable { onNavigate(tool.route) }
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(tool.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(tool.icon, contentDescription = null, tint = tool.color, modifier = Modifier.size(24.dp))
                            }
                            Text(
                                text = tool.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = tool.subtitle,
                                color = Color(0xFFA0A5B5),
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // 4. Trending Video Templates
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Editable Templates", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "View All", color = Color(0xFF7C4DFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    items(TemplatesCatalog.allTemplates.take(5)) { tmpl ->
                        TemplateCardItem(
                            template = tmpl,
                            onClick = { viewModel.loadTemplate(tmpl) }
                        )
                    }
                }
            }

            // 5. Recent Projects (Room Database)
            item {
                Text(
                    text = "My Projects (${allProjects.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(allProjects) { project ->
                ProjectListItem(
                    project = project,
                    onClick = {
                        viewModel.loadProject(project.id)
                        onNavigate("editor")
                    },
                    onFavoriteToggle = { viewModel.toggleFavorite(project) },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (showImportDialog) {
            ModalBottomSheet(
                onDismissRequest = { showImportDialog = false },
                containerColor = Color(0xFF141724),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF454D6B))
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Create New Project",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select media from your phone to start editing on the multi-layer timeline",
                        color = Color(0xFFA0A5B5),
                        fontSize = 13.sp
                    )

                    // 1. Select Video
                    Surface(
                        color = Color(0xFF1E2235),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImportDialog = false
                                pickVideosLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF7C4DFF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(26.dp))
                            }
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text("Select Video(s)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Import 1 or multiple videos from phone gallery", color = Color(0xFFA0A5B5), fontSize = 12.sp)
                            }
                        }
                    }

                    // 2. Select Photos
                    Surface(
                        color = Color(0xFF1E2235),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImportDialog = false
                                pickPhotosLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF00ADB5).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF00ADB5), modifier = Modifier.size(26.dp))
                            }
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text("Select Photo(s)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Create photo slideshow or video story", color = Color(0xFFA0A5B5), fontSize = 12.sp)
                            }
                        }
                    }

                    // 3. Select Audio
                    Surface(
                        color = Color(0xFF1E2235),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C324B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImportDialog = false
                                pickAudioLauncher.launch(arrayOf("audio/*"))
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(26.dp))
                            }
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text("Select Audio / Music", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Import background music or voice tracks", color = Color(0xFFA0A5B5), fontSize = 12.sp)
                            }
                        }
                    }

                    // 4. Blank Project
                    Surface(
                        color = Color(0xFF161A26),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImportDialog = false
                                viewModel.createNewProject("Untitled Project")
                                onNavigate("editor")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFA0A5B5), modifier = Modifier.size(22.dp))
                            Text("Blank Timeline Project (Empty Canvas)", color = Color(0xFFA0A5B5), fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun TemplateCardItem(
    template: TemplateItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141724))
            .border(1.dp, Color(0xFF222638), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(template.colorGradient.first), Color(template.colorGradient.second))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0x66000000),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = template.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${template.durationSeconds}s • ${template.category}",
                color = Color(0xFFA0A5B5),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ProjectListItem(
    project: Project,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Color(0xFF141724),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C324B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFF00E5FF))
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = project.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${project.aspectRatio.label.take(4)} • ${project.durationMs / 1000}s • ${project.tracks.size} tracks",
                        color = Color(0xFFA0A5B5),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Row {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (project.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (project.isFavorite) Color(0xFFFF5252) else Color(0xFFA0A5B5)
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF6B728E))
                }
            }
        }
    }
}
