package com.example.data.model

object TemplatesCatalog {

    val categories = listOf(
        "All",
        "Reels",
        "Shorts",
        "YouTube",
        "Travel",
        "Birthday",
        "Wedding",
        "Cinematic",
        "Festival",
        "Business",
        "Music",
        "Trending"
    )

    val allTemplates = listOf(
        TemplateItem(
            id = "tmpl_reels_velocity",
            title = "Viral Beat Sync Velocity",
            category = "Reels",
            durationSeconds = 15,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "High energy speed ramps synced with heavy bass drops and flash transitions.",
            musicGenre = "Phonk Trap",
            tags = listOf("Fast", "BeatSync", "Trending"),
            colorGradient = Pair(0xFFFF0055, 0xFF7C4DFF)
        ),
        TemplateItem(
            id = "tmpl_travel_jharkhand",
            title = "Jharkhand Hills & Falls",
            category = "Travel",
            durationSeconds = 30,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "Cinematic aerial shots with warm sunset color grading, subtle voiceover and lofi audio.",
            musicGenre = "Ambient Indian Acoustic",
            tags = listOf("Nature", "Cinematic", "Wanderlust"),
            colorGradient = Pair(0xFF00B09B, 0xFF96C93D)
        ),
        TemplateItem(
            id = "tmpl_shorts_tech",
            title = "Tech Gadget Unboxing",
            category = "Shorts",
            durationSeconds = 20,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "Clean modern typography, animated kinetic subtitles, and macro zoom reveals.",
            musicGenre = "Cyber Electronic",
            tags = listOf("Tech", "Clean", "Captions"),
            colorGradient = Pair(0xFF2193B0, 0xFF6DD5ED)
        ),
        TemplateItem(
            id = "tmpl_yt_intro",
            title = "Cinematic YouTube Intro",
            category = "YouTube",
            durationSeconds = 12,
            aspect = AspectRatioType.LANDSCAPE_16_9,
            description = "Hollywood letterbox layout with orchestral crescendo and glowing 3D logo reveal.",
            musicGenre = "Epic Orchestral",
            tags = listOf("YouTube", "Intro", "16:9"),
            colorGradient = Pair(0xFF8E2DE2, 0xFF4A00E0)
        ),
        TemplateItem(
            id = "tmpl_birthday_glow",
            title = "Magical Birthday Celebration",
            category = "Birthday",
            durationSeconds = 18,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "Glitter sparkles, joyful pop transitions, and celebratory confetti overlays.",
            musicGenre = "Upbeat Pop",
            tags = listOf("Celebration", "Confetti", "Memories"),
            colorGradient = Pair(0xFFF857A6, 0xFFFF5858)
        ),
        TemplateItem(
            id = "tmpl_wedding_royal",
            title = "Royal Wedding Highlights",
            category = "Wedding",
            durationSeconds = 45,
            aspect = AspectRatioType.LANDSCAPE_16_9,
            description = "Dreamy soft focus, golden hour tint, elegant serif subtitles, and slow-motion romance.",
            musicGenre = "Romantic Strings",
            tags = listOf("Royal", "Elegant", "Love"),
            colorGradient = Pair(0xFFC0A080, 0xFF8A5A36)
        ),
        TemplateItem(
            id = "tmpl_festival_diwali",
            title = "Diwali Festival of Lights",
            category = "Festival",
            durationSeconds = 25,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "Glowing diya lights, vibrant warm hues, sparkler animations, and festive music.",
            musicGenre = "Festive Fusion",
            tags = listOf("Diwali", "Lights", "India"),
            colorGradient = Pair(0xFFFF8008, 0xFFFFC837)
        ),
        TemplateItem(
            id = "tmpl_biz_pitch",
            title = "Modern Startup Product Pitch",
            category = "Business",
            durationSeconds = 30,
            aspect = AspectRatioType.LANDSCAPE_16_9,
            description = "Clean corporate typography, animated statistic counters, and professional pacing.",
            musicGenre = "Modern Corporate Minimal",
            tags = listOf("Pitch", "Startup", "Professional"),
            colorGradient = Pair(0xFF141E30, 0xFF243B55)
        ),
        TemplateItem(
            id = "tmpl_music_visualizer",
            title = "Audio Waveform Visualizer",
            category = "Music",
            durationSeconds = 30,
            aspect = AspectRatioType.SQUARE_1_1,
            description = "Pulsing neon audio spectrum reacting to bass kicks with spinning vinyl album art.",
            musicGenre = "Electronic EDM",
            tags = listOf("Spectrum", "Bass", "CoverArt"),
            colorGradient = Pair(0xFF11998E, 0xFF38EF7D)
        ),
        TemplateItem(
            id = "tmpl_trending_glitch",
            title = "Cyberpunk Glitch Reel",
            category = "Trending",
            durationSeconds = 15,
            aspect = AspectRatioType.PORTRAIT_9_16,
            description = "Fast cuts, chromatic aberration, neon subtitles, and futuristic sound design.",
            musicGenre = "Synthwave Dark",
            tags = listOf("Cyberpunk", "Trending", "Neon"),
            colorGradient = Pair(0xFFB06AB3, 0xFF4568DC)
        )
    )
}
