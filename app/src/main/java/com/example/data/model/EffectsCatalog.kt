package com.example.data.model

data class StudioEffect(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val accentColor: Long,
    val isPremium: Boolean = false
)

data class StudioTransition(
    val id: String,
    val name: String,
    val category: String,
    val defaultDurationMs: Long = 500L,
    val isPremium: Boolean = false
)

object EffectsCatalog {

    val categories = listOf(
        "All",
        "Cinematic",
        "Glitch",
        "Retro",
        "VHS",
        "Neon",
        "Light",
        "Flash",
        "Shake",
        "Zoom",
        "Blur",
        "Distortion",
        "RGB",
        "Beauty",
        "Dream",
        "Film",
        "AI",
        "Social Media",
        "Trending"
    )

    // Complete 100+ categorized studio effects
    val allEffects: List<StudioEffect> by lazy {
        val list = mutableListOf<StudioEffect>()

        // 1. Cinematic (10)
        val cinematic = listOf(
            "Anamorphic Flare", "Teal & Orange Grade", "Hollywood 35mm", "Blockbuster Noir",
            "Golden Hour Bloom", "Letterbox Cinema", "Cinematic Mist", "Film Halation",
            "Subtle Lens Dirt", "IMAX Sharpness"
        )
        cinematic.forEachIndexed { i, name ->
            list.add(StudioEffect("cine_$i", name, "Cinematic", "Film-grade atmospheric styling", 0xFFE5A93B))
        }

        // 2. Glitch (10)
        val glitch = listOf(
            "Cyber Glitch", "Data Moshing", "Pixel Sorter", "Sync Tear",
            "Signal Jitter", "Corrupted Feed", "Hex Crash", "Block Artifact",
            "CRT Static", "Binary Noise"
        )
        glitch.forEachIndexed { i, name ->
            list.add(StudioEffect("glitch_$i", name, "Glitch", "Digital displacement distortion", 0xFFFF0055))
        }

        // 3. Retro & VHS (10)
        val retro = listOf(
            "VHS Camcorder 1998", "Tape Rewind Distortion", "80s Synth Aesthetic", "VCR Tracking Error",
            "Scanline Phosphor", "Low-Fi Casette", "Super 8 Vintage", "Monochrome 1920",
            "Sepia Nostalgia", "Warm Kodachrome"
        )
        retro.forEachIndexed { i, name ->
            list.add(StudioEffect("retro_$i", name, if (i < 5) "VHS" else "Retro", "Authentic analog warmth and scanlines", 0xFFFF8C00))
        }

        // 4. Neon & Light (10)
        val neonLight = listOf(
            "Neon Cyber Edge", "Electric Cyan Pulse", "Ultraviolet Glow", "Prism Refraction",
            "Anamorphic Streak", "Sun Flare Leak", "Soft Lens Diffuse", "Rainbow Halo",
            "Glow Aura", "Strobe Beam"
        )
        neonLight.forEachIndexed { i, name ->
            list.add(StudioEffect("neon_$i", name, if (i < 5) "Neon" else "Light", "Luminous glow and spectral leaks", 0xFF00E5FF))
        }

        // 5. Flash & Shake (10)
        val flashShake = listOf(
            "Beat Flash White", "Invert Black Flash", "Bass Impact Shake", "Camera Quake",
            "Earthquake Tremor", "Smooth Handheld Drift", "Micro Jitter", "RGB Flash Pop",
            "Heavy Impact Strobe", "Whip Strobe"
        )
        flashShake.forEachIndexed { i, name ->
            list.add(StudioEffect("flash_$i", name, if (i < 4) "Flash" else "Shake", "Energetic rhythm impacts", 0xFFFFEA00))
        }

        // 6. Zoom & Blur (10)
        val zoomBlur = listOf(
            "Radial Velocity Zoom", "Crash Zoom Pop", "Optical Dolly Zoom", "Slow Breathing Zoom",
            "Gaussian Soften", "Directional Motion Blur", "Tilt Shift Miniature", "Bokeh Hexagon",
            "Ghosting Trail", "Defocus Pulse"
        )
        zoomBlur.forEachIndexed { i, name ->
            list.add(StudioEffect("zoom_$i", name, if (i < 4) "Zoom" else "Blur", "Dynamic depth of field and velocity", 0xFF7C4DFF))
        }

        // 7. Distortion & RGB (10)
        val distRgb = listOf(
            "RGB Split Chromatic", "Prism Edge Aberration", "Fisheye Barrel", "Wave Ripple",
            "Vortex Swirl", "Mirror Kaleidoscope", "Liquify Pull", "Sphere Bulge",
            "Anaglyph 3D", "Spectral Offset"
        )
        distRgb.forEachIndexed { i, name ->
            list.add(StudioEffect("dist_$i", name, if (i < 5) "RGB" else "Distortion", "Geometric and chromatic warp", 0xFFFF2E93))
        }

        // 8. Beauty & Dream (10)
        val beautyDream = listOf(
            "Skin Smooth Silk", "Eye Sparkle Enhance", "Dreamy Cloud Softener", "Ethereal Glow",
            "Angel Flare", "Starry Twinkle", "Warm Skin Glow", "Vivid Eye Pop",
            "Pastel Fantasy", "Heavenly Radiance"
        )
        beautyDream.forEachIndexed { i, name ->
            list.add(StudioEffect("beauty_$i", name, if (i < 4) "Beauty" else "Dream", "Radiant complexion and dreamscapes", 0xFFFF80BF))
        }

        // 9. Film & Film Grain (10)
        val film = listOf(
            "Kodak 5207 16mm", "Fujifilm Eterna 250D", "Coarse Silver Grain", "Dust & Scratches",
            "Film Gate Weave", "Perforation Sprocket", "Bleach Bypass", "Technicolor 3-Strip",
            "Warm Tungsten Grain", "Vintage Grain Fine"
        )
        film.forEachIndexed { i, name ->
            list.add(StudioEffect("film_$i", name, "Film", "Classic analog film stock emulation", 0xFFC0A080))
        }

        // 10. AI & Trending & Social Media (15)
        val aiTrending = listOf(
            "AI Background Glow", "AI Cyberpunk Relight", "AI Anime Shading", "AI Motion Trail",
            "AI Depth Parallax", "AI Glow Silhouette", "Trending Velocity Ramp", "Trending Viral Zoom",
            "Trending Beat Shake", "Reels Kinetic Pop", "Shorts Attention Hook", "TikTok Strobe Cut",
            "Viral Glitch Zoom", "Hyperlapse Smooth", "Dynamic Speed Blur"
        )
        aiTrending.forEachIndexed { i, name ->
            val cat = when {
                i < 6 -> "AI"
                i < 10 -> "Trending"
                else -> "Social Media"
            }
            list.add(StudioEffect("ai_trend_$i", name, cat, "High engagement creator effects", 0xFF00FFCC))
        }

        list
    }

    // 25+ Transitions across categories
    val allTransitions: List<StudioTransition> = listOf(
        StudioTransition("tr_fade", "Fade to Black", "Fade", 400L),
        StudioTransition("tr_fade_white", "Fade to White", "Fade", 300L),
        StudioTransition("tr_dissolve", "Cross Dissolve", "Dissolve", 500L),
        StudioTransition("tr_zoom_in", "Zoom In Punch", "Zoom", 350L),
        StudioTransition("tr_zoom_out", "Zoom Out Drift", "Zoom", 400L),
        StudioTransition("tr_slide_left", "Slide Left", "Slide", 400L),
        StudioTransition("tr_slide_right", "Slide Right", "Slide", 400L),
        StudioTransition("tr_slide_up", "Slide Up", "Slide", 400L),
        StudioTransition("tr_push_left", "Push Left", "Push", 350L),
        StudioTransition("tr_spin_clock", "Spin Clockwise", "Spin", 450L),
        StudioTransition("tr_spin_counter", "Spin Counter", "Spin", 450L),
        StudioTransition("tr_motion_blur", "Directional Blur", "Blur", 300L),
        StudioTransition("tr_glitch", "Cyber Glitch Slice", "Glitch", 250L),
        StudioTransition("tr_data_tear", "Data Tear", "Glitch", 200L),
        StudioTransition("tr_flash_white", "Beat Flash", "Flash", 200L),
        StudioTransition("tr_light_leak", "Warm Light Leak", "Light Leak", 600L),
        StudioTransition("tr_anamorphic", "Anamorphic Streak", "Light Leak", 500L),
        StudioTransition("tr_camera_whip", "Camera Whip Pan", "Camera", 250L),
        StudioTransition("tr_camera_shake", "Impact Shake", "Camera", 300L),
        StudioTransition("tr_cinematic_black", "Cinematic Letterbox Fade", "Cinematic", 500L),
        StudioTransition("tr_film_burn", "Vintage Film Burn", "Cinematic", 600L),
        StudioTransition("tr_ai_morph", "AI Seamless Morph", "AI Transitions", 500L, isPremium = true),
        StudioTransition("tr_ai_liquid", "AI Liquid Warp", "AI Transitions", 450L, isPremium = true),
        StudioTransition("tr_ai_zoom_portal", "AI Infinite Zoom Portal", "AI Transitions", 600L, isPremium = true)
    )
}
