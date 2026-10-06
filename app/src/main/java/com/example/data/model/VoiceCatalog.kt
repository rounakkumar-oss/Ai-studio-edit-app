package com.example.data.model

data class VoiceProfile(
    val id: String,
    val name: String,
    val gender: String, // Male, Female, Non-Binary
    val category: String, // Narrator, News, Cinematic, Storytelling, Character, Emotional, Funny, Deep, Soft
    val language: String, // English, Hindi, Hinglish, Spanish, French, Regional
    val accentTag: String,
    val samplePreviewText: String,
    val defaultPitch: Float = 1.0f,
    val defaultSpeed: Float = 1.0f
)

object VoiceCatalog {

    val voiceCategories = listOf(
        "All",
        "Narrator",
        "News",
        "Cinematic",
        "Storytelling",
        "Character",
        "Emotional",
        "Funny",
        "Deep",
        "Soft"
    )

    val supportedLanguages = listOf(
        "English (US)",
        "English (India)",
        "English (UK)",
        "Hindi (India)",
        "Hinglish (Conversational)",
        "Spanish (Global)",
        "French (Europe)",
        "German",
        "Japanese",
        "Marathi",
        "Bengali",
        "Tamil",
        "Telugu"
    )

    val voiceChangerEffects = listOf(
        "Normal Studio",
        "Studio Mic Pro",
        "Deep Bass Radio",
        "Helium Balloon",
        "Cybernetic Robot",
        "Megaphone Public",
        "Telephone Lo-Fi",
        "Ethereal Cave Echo",
        "Alien Synth",
        "Monster Titan"
    )

    // Curated catalog with 100+ voice personas
    val allVoices: List<VoiceProfile> by lazy {
        val list = mutableListOf<VoiceProfile>()

        // 1. Narrators & Documentaries
        val narrators = listOf(
            Triple("Aarav", "Male", "Hindi (India)"),
            Triple("David", "Male", "English (US)"),
            Triple("Elena", "Female", "English (UK)"),
            Triple("Pooja", "Female", "Hindi (India)"),
            Triple("Arthur", "Male", "English (UK)"),
            Triple("Maya", "Female", "English (India)"),
            Triple("Vikram", "Male", "Hinglish (Conversational)"),
            Triple("Chloe", "Female", "French (Europe)"),
            Triple("Mateo", "Male", "Spanish (Global)"),
            Triple("Ananya", "Female", "Hindi (India)")
        )
        narrators.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("narrator_$i", name, gender, "Narrator", lang, "Crisp & Articulate", "Welcome to the story that shaped our generation."))
        }

        // 2. News & Anchors
        val news = listOf(
            Triple("Siddharth", "Male", "Hindi (India)"),
            Triple("Rachel", "Female", "English (US)"),
            Triple("Kabir", "Male", "Hinglish (Conversational)"),
            Triple("Olivia", "Female", "English (UK)"),
            Triple("Tanvi", "Female", "Hindi (India)"),
            Triple("Marcus", "Male", "English (US)"),
            Triple("Sofia", "Female", "Spanish (Global)"),
            Triple("Rohan", "Male", "English (India)"),
            Triple("Isha", "Female", "Hindi (India)"),
            Triple("Lucas", "Male", "English (US)")
        )
        news.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("news_$i", name, gender, "News", lang, "Authoritative & Fast", "Breaking news tonight as AI Studio revolutionizes creative media."))
        }

        // 3. Cinematic & Trailer Voices
        val cinematic = listOf(
            Triple("Morgan", "Male", "English (US)"),
            Triple("Devraj", "Male", "Hindi (India)"),
            Triple("Victoria", "Female", "English (UK)"),
            Triple("Bhim", "Male", "Hindi (India)"),
            Triple("Alexander", "Male", "English (US)"),
            Triple("Kalyani", "Female", "Hindi (India)"),
            Triple("Gabriel", "Male", "Spanish (Global)"),
            Triple("Scarlett", "Female", "English (US)"),
            Triple("Aryan", "Male", "Hinglish (Conversational)"),
            Triple("Thorin", "Male", "English (UK)")
        )
        cinematic.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("cine_voice_$i", name, gender, "Cinematic", lang, "Gravelly & Resonant", "In a world divided by darkness, only one creator stood tall.", defaultPitch = 0.85f))
        }

        // 4. Storytelling & Bedtime
        val storytelling = listOf(
            Triple("Grandmother Sarita", "Female", "Hindi (India)"),
            Triple("Uncle Arthur", "Male", "English (UK)"),
            Triple("Meera", "Female", "Hindi (India)"),
            Triple("Lucas", "Male", "English (US)"),
            Triple("Pari", "Female", "Hinglish (Conversational)"),
            Triple("Clara", "Female", "French (Europe)"),
            Triple("Gopal", "Male", "Hindi (India)"),
            Triple("Evelyn", "Female", "English (US)"),
            Triple("Arjun", "Male", "English (India)"),
            Triple("Tara", "Female", "Hindi (India)")
        )
        storytelling.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("story_$i", name, gender, "Storytelling", lang, "Gentle & Expressive", "Once upon a time, far beyond the emerald hills of Netarhat...", defaultPitch = 0.95f))
        }

        // 5. Character & Gaming Voices
        val characters = listOf(
            Triple("Shadow Ninja", "Male", "English (US)"),
            Triple("Cyber Punk Dex", "Male", "English (US)"),
            Triple("Anime Princess Sakura", "Female", "Japanese"),
            Triple("Desi Gamer Bunty", "Male", "Hinglish (Conversational)"),
            Triple("Wizard Eldrin", "Male", "English (UK)"),
            Triple("Pixie Spark", "Female", "English (US)"),
            Triple("Captain Jack", "Male", "English (UK)"),
            Triple("Mecha Titan 9", "Male", "English (US)"),
            Triple("Chulbul Cop", "Male", "Hindi (India)"),
            Triple("Space Commander Ava", "Female", "English (US)")
        )
        characters.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("char_$i", name, gender, "Character", lang, "Dynamic & Funky", "Ready up team, the zone is closing in fast!"))
        }

        // 6. Emotional & Dramatic Voices
        val emotional = listOf(
            Triple("Diya (Heartfelt)", "Female", "Hindi (India)"),
            Triple("Julian (Sincere)", "Male", "English (US)"),
            Triple("Rhea (Vulnerable)", "Female", "English (India)"),
            Triple("Karan (Passionate)", "Male", "Hindi (India)"),
            Triple("Isabella (Romantic)", "Female", "Spanish (Global)"),
            Triple("Samir (Inspirational)", "Male", "Hinglish (Conversational)"),
            Triple("Grace (Uplifting)", "Female", "English (US)"),
            Triple("Advait (Poetic)", "Male", "Hindi (India)"),
            Triple("Camille (Warm)", "Female", "French (Europe)"),
            Triple("Noah (Reflective)", "Male", "English (US)")
        )
        emotional.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("emot_$i", name, gender, "Emotional", lang, "Vulnerable & Warm", "Every moment we spent together remains etched in my heart forever."))
        }

        // 7. Funny & Quirky Voices
        val funny = listOf(
            Triple("Chintu Comic", "Male", "Hindi (India)"),
            Triple("Giggle Gary", "Male", "English (US)"),
            Triple("Sassy Sarah", "Female", "English (US)"),
            Triple("Pappu Tapori", "Male", "Hinglish (Conversational)"),
            Triple("Professor Quirk", "Male", "English (UK)"),
            Triple("Munna Standup", "Male", "Hindi (India)"),
            Triple("Bubbly Becky", "Female", "English (US)"),
            Triple("Chatty Charlie", "Male", "English (UK)"),
            Triple("Babli Gossip", "Female", "Hindi (India)"),
            Triple("Silly Steve", "Male", "English (US)")
        )
        funny.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("funny_$i", name, gender, "Funny", lang, "Playful & Upbeat", "Wait, did you really just delete the master video track? Haha!"))
        }

        // 8. Deep Voices
        val deep = listOf(
            Triple("Titan Bass", "Male", "English (US)"),
            Triple("Ranveer (Sub-bass)", "Male", "Hindi (India)"),
            Triple("Goliath", "Male", "English (UK)"),
            Triple("Prithvi (Resonant)", "Male", "Hindi (India)"),
            Triple("Deep Space Vox", "Male", "English (US)"),
            Triple("Jagdish (Radio)", "Male", "Hindi (India)"),
            Triple("Baritone Bruce", "Male", "English (US)"),
            Triple("Samrat", "Male", "Hindi (India)"),
            Triple("Vortex Low", "Male", "English (US)"),
            Triple("Colossus", "Male", "English (UK)")
        )
        deep.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("deep_$i", name, gender, "Deep", lang, "Ultra Deep & Sub-bass", "Feel the resonance reverberating in your core.", defaultPitch = 0.75f))
        }

        // 9. Soft & Whisper Voices
        val soft = listOf(
            Triple("Luna (ASMR)", "Female", "English (US)"),
            Triple("Kavya (Gentle)", "Female", "Hindi (India)"),
            Triple("Whisper Willow", "Female", "English (UK)"),
            Triple("Aanya (Calm)", "Female", "Hindi (India)"),
            Triple("Zephyr (Breeze)", "Male", "English (US)"),
            Triple("Nisha (Night)", "Female", "Hindi (India)"),
            Triple("Serena", "Female", "English (US)"),
            Triple("Shanti", "Female", "Hindi (India)"),
            Triple("Aura", "Female", "French (Europe)"),
            Triple("Breeze Boi", "Male", "English (US)")
        )
        soft.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("soft_$i", name, gender, "Soft", lang, "Intimate & Soothing", "Close your eyes, breathe gently, and let the stress dissolve away."))
        }

        // 10. Regional Indian & Multilingual Voices (10)
        val regional = listOf(
            Triple("Tanmay (Marathi)", "Male", "Marathi"),
            Triple("Swati (Marathi)", "Female", "Marathi"),
            Triple("Debasish (Bengali)", "Male", "Bengali"),
            Triple("Shrabani (Bengali)", "Female", "Bengali"),
            Triple("Karthik (Tamil)", "Male", "Tamil"),
            Triple("Kavitha (Tamil)", "Female", "Tamil"),
            Triple("Sai (Telugu)", "Male", "Telugu"),
            Triple("Keerthi (Telugu)", "Female", "Telugu"),
            Triple("Hans (German)", "Male", "German"),
            Triple("Ren (Japanese)", "Male", "Japanese")
        )
        regional.forEachIndexed { i, (name, gender, lang) ->
            list.add(VoiceProfile("reg_$i", name, gender, "Narrator", lang, "Native Regional Dialect", "Namaskar, this is your regional voice creation experience."))
        }

        list
    }
}
