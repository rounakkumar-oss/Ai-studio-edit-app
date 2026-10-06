# 📥 DOWNLOAD LATEST APK

### 👉 [**Download Latest APK (`AI-Studio-v1.0.0.apk`) from GitHub Releases**](https://github.com/aistudio-creator/ai-studio/releases/latest)

- **APK** = Normal users install this directly on their Android device without programming knowledge or Android Studio.
- **Source ZIP** = Developers use this to modify, explore, and build the project in Android Studio.

---

# 🎬 AI Studio — Next-Gen AI Video, Image & Audio Creation Suite

[![Latest Release](https://img.shields.io/badge/Release-v1.0.0_Stable-7C4DFF?style=for-the-badge&logo=android)](https://github.com/aistudio-creator/ai-studio/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android_7.0+-00E5FF?style=for-the-badge&logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Kotlin_+_Jetpack_Compose-FF5370?style=for-the-badge&logo=kotlin)](https://developer.android.com/jetpack/compose)

> **AI Studio** is an advanced, production-grade Android video, image, audio, and content creation studio inspired by professional editors like CapCut, but enhanced with powerful built-in AI tools. Designed for **both normal users and developers**.

---

## ⚡ QUICK START: CHOOSE YOUR PATH

### 👤 1. FOR NORMAL USERS (NO CODING REQUIRED)

You do **NOT** need Android Studio, Kotlin, Gradle, or any programming knowledge.

👉 [**⬇️ DOWNLOAD LATEST APK (AI-Studio-v1.0.0.apk)**](https://github.com/aistudio-creator/ai-studio/releases/latest)

#### Simple 4-Step Installation:
1. Open [**GitHub Releases**](https://github.com/aistudio-creator/ai-studio/releases/latest).
2. Download **`AI-Studio-v1.0.0.apk`**.
3. Tap the downloaded APK in your device notifications or Downloads folder.
4. If Android prompts *"Install unknown apps"*, tap **Settings** and allow it, then tap **Install** and open **AI Studio**.

---

### 💻 2. FOR DEVELOPERS & CONTRIBUTORS

Developers can download the complete source code or clone the repository to modify, build, or contribute.

👉 [**📦 DOWNLOAD SOURCE CODE (AI-Studio-v1.0.0-Source.zip)**](https://github.com/aistudio-creator/ai-studio/releases/latest)

```bash
# Or clone via Git:
git clone https://github.com/aistudio-creator/ai-studio.git
cd ai-studio

# Build APK using Gradle:
gradle assembleDebug
```

---

## 🌟 CORE HIGHLIGHTS & FEATURES

### 1. 🎛️ Multi-Layer Professional Video Timeline
- **Multi-Track Support**: Video (A-Roll & B-Roll), Audio (Music, Voiceover, SFX), Text & Animated Captions, and Effects.
- **Editing Tools**: Split, Cut, Trim, Duplicate, Reverse, Speed Ramping (0.2x to 5.0x), Volume, Opacity, and Layer reordering.
- **Advanced Effects**: Chroma key (green screen), AI Background Removal cutout, Masking, and Blending modes.
- **Undo / Redo**: Instant state stack recovery.

### 2. 🤖 AI Director Engine
- Enter an idea like *"Make a 30-second Jharkhand travel video"* or *"Fast-paced tech product launch"*.
- Automatically breaks down the idea into scenes, selects camera angles, detects music beats, adds transitions, and generates an **editable timeline project**.

### 3. 🎙️ AI Voice Studio (100+ Styles)
- Natural text-to-speech with **100+ voices** across categories: Narrator, News Anchor, Cinematic Trailer, Storyteller, Character, Deep Voice, and Soft Whisper.
- Multilingual support for **Hindi, English, Hinglish, Marathi, Bengali, Tamil, Telugu**, Spanish, French, and Japanese.
- Voice controls for Speed, Pitch, and Voice Changer effects (Studio Mic, Helium, Cyber Robot, Megaphone).

### 4. 🎵 Audio & AI Music Studio
- Multi-track audio mixer with waveform visualization.
- Beat & BPM grid calculation with downbeat alignment.
- Equalizer presets (Bass Boost, Vocal Clarity, Acoustic Warmth).
- Sound effects pack (Whoosh, Bass Drop, Vinyl Scratch, Cyber Glitch).

### 5. ✨ 100+ Effects & 25+ Transitions Library
- **Categories**: Cinematic, Glitch, Retro VHS, Neon Glow, Flash, Shake, Zoom Velocity, Blur, Distortion, RGB Split, Beauty, Film Grain, and AI Trends.
- Searchable and categorized with instant live preview.
- Transitions: Cross Dissolve, Zoom In/Out, Slide, Whip Pan, Light Leak, Glitch, and AI Morph.

### 6. 🎨 Color Grading & Cinematic LUTs
- Brightness, Contrast, Saturation, Temperature, Tint, and Vignette controls.
- Cinematic LUT Presets: Teal & Orange, Cyberpunk Neon, Hollywood Noir, Warm Sunset, and Retro VHS.

### 7. 🖼️ AI Thumbnail Maker
- High-CTR presets for YouTube (16:9) and Instagram Reels / Shorts (9:16).
- High-contrast gradient backgrounds, custom stickers, viral badges (`🔥 VIRAL`, `😱 SHOCKING`, `🚀 10X SPEED`), and glow effects.

### 8. 💬 Auto Captions & Subtitles
- Speech-to-text subtitle generation with word-by-word timing.
- Karaoke-style word highlight animations.
- Multilingual translation between Hindi, English, and Spanish.

### 9. 🚀 Export Engine
- Export resolutions: **720p, 1080p Full HD, 1440p 2K, and 4K UHD**.
- Frame rates: **24 fps (Cinema), 30 fps, and 60 fps**.
- Real-time frame progress bar with cancelation support and direct saving to device Movies storage.

---

## 🏛️ SYSTEM ARCHITECTURE & CODEBASE STRUCTURE

```
app/src/main/java/com/example/
├── MainActivity.kt                # Root Navigation & Edge-to-Edge Activity
├── ai/
│   ├── AIProviders.kt             # Clean interfaces (Script, Video, Voice, Music, Captions)
│   ├── GeminiAiService.kt         # Gemini 2.5 Flash implementation with clear status reporting
│   └── AIDirectorEngine.kt        # Multi-layer auto-editing & scene sequencing engine
├── audio/
│   └── AudioEngine.kt             # Waveform synthesis, Equalizer presets, Beat detection
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt         # Room Database configuration
│   │   ├── ProjectDao.kt          # Flow-enabled reactive CRUD queries
│   │   └── ProjectEntity.kt       # Persistent JSON-serialized project entity
│   ├── model/
│   │   ├── ProjectModels.kt       # Domain models (Project, TimelineClip, Track, ColorGrading)
│   │   ├── EffectsCatalog.kt      # 100+ Effects & 25+ Transitions Catalog
│   │   ├── TemplatesCatalog.kt    # Trending editable template presets
│   │   └── VoiceCatalog.kt        # 100+ TTS Voice personas & languages
│   └── repository/
│       └── ProjectRepository.kt   # Repository with starter project seeding & auto-save
├── ui/
│   ├── editor/
│   │   ├── VideoEditorScreen.kt   # Main Editor container
│   │   ├── VideoPreviewPlayer.kt  # GPU/Canvas multi-layer real-time preview player
│   │   ├── TimelineView.kt        # Interactive multi-layer track timeline with zoom & scrubbing
│   │   ├── EditorToolbars.kt      # TopBar and Bottom Action toolbars (Split, Speed, Filter, etc.)
│   │   └── EditorModals.kt        # Effects, Transitions, and Color Grading bottom sheets
│   ├── export/
│   │   └── ExportDialog.kt        # Resolution, FPS, Bitrate encoder progress sheet
│   ├── home/
│   │   └── HomeScreen.kt          # Dashboard with AI Command Bar, Tools grid, Projects & Templates
│   ├── settings/
│   │   └── SettingsScreen.kt      # AI engine status, low-RAM optimizations, update checker
│   ├── theme/
│   │   ├── Color.kt               # Obsidian dark & neon studio palette
│   │   ├── Theme.kt               # Material 3 dark studio theme
│   │   └── Type.kt                # Typography styles
│   ├── tools/
│   │   ├── AIDirectorScreen.kt    # Idea-to-Cut director screen
│   │   ├── AIVideoScreen.kt       # Text-to-Video generation screen
│   │   ├── AIVoiceScreen.kt       # 100+ TTS voice synthesizer screen
│   │   ├── AIMusicScreen.kt       # BGM & beat generator screen
│   │   ├── AutoCaptionsScreen.kt  # Timed subtitle transcriber & translator screen
│   │   └── ThumbnailMakerScreen.kt# High-CTR cover art creator screen
│   └── viewmodel/
│       └── StudioViewModel.kt     # Unified MVVM state management
└── updater/
    └── GitHubUpdateChecker.kt     # In-app update checker querying GitHub Releases
```

---

## 🔒 SECURITY & PRIVACY
- **Zero API Key Leakage**: Keys are injected at build/runtime via AI Studio Secrets and `BuildConfig`, never hardcoded into repository files.
- **Zero-Permission Media Picker**: Uses Android's modern photo picker (`PickVisualMedia`) without requiring invasive `READ_EXTERNAL_STORAGE` permissions.
- **Local-First Storage**: User projects and metadata are encrypted and persisted on-device via SQLite Room Database.

---

## 📄 LICENSE
Distributed under the Apache 2.0 License. See `LICENSE` for details.
