package com.music.bitchord.party.model

/**
 * Specialty vibes inspired by NAUX.party for high-energy sets.
 */
enum class SpecialtyVibe(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val defaultEnergy: Float,
    val associatedKeywords: List<String>
) {
    UNDERGROUND_DHH(
        id = "underground_dhh",
        title = "Underground DHH Rap Music",
        subtitle = "Raw bars, gully beats, heavy 808s",
        emoji = "🎙️",
        defaultEnergy = 8.5f,
        associatedKeywords = listOf("dhh", "desi hip hop", "gully", "divine", "seedhe maut", "kalamkaar", "kr$na", "emiway", "mc stan", "raw")
    ),
    HUSTLE(
        id = "hustle",
        title = "Hustle",
        subtitle = "Punchy trap, grind mentality, motivation cyphers",
        emoji = "⚡",
        defaultEnergy = 7.8f,
        associatedKeywords = listOf("hustle", "trap", "grind", "motivation", "cypher", "drill", "banger")
    ),
    LEGACY_SHOW(
        id = "legacy_show",
        title = "Legacy Show Music",
        subtitle = "Stadium anthems, iconic stage hits",
        emoji = "🏆",
        defaultEnergy = 8.0f,
        associatedKeywords = listOf("anthem", "stadium", "classic", "legacy", "concert", "crowd", "live")
    ),
    HARDCORE(
        id = "hardcore",
        title = "Hardcore",
        subtitle = "Maximum intensity, drill/rage energy, 10/10 distortion",
        emoji = "💀",
        defaultEnergy = 9.8f,
        associatedKeywords = listOf("hardcore", "drill", "rage", "distortion", "heavy", "moshpit", "max")
    ),
    DESI_DANCE(
        id = "desi_dance",
        title = "Desi Dance",
        subtitle = "High-energy commercial beats, club wedding bangers",
        emoji = "💃",
        defaultEnergy = 8.2f,
        associatedKeywords = listOf("dance", "bollywood", "desi", "remix", "club", "party")
    ),
    PUNJABI_PARTY(
        id = "punjabi_party",
        title = "Punjabi Party",
        subtitle = "Bass-boosted dhol, upbeat Punjabi anthems",
        emoji = "🔥",
        defaultEnergy = 8.8f,
        associatedKeywords = listOf("punjabi", "dhol", "ap dhillon", "sidhu", "karan aujla", "diljit", "bhangra")
    );

    companion object {
        fun fromId(id: String): SpecialtyVibe? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}

/**
 * Energy progression based on party phase / time of night.
 */
enum class TimeOfNight(
    val id: String,
    val title: String,
    val minEnergy: Float,
    val maxEnergy: Float,
    val targetEnergy: Float
) {
    WARM_UP("warm_up", "Warm-up", 4.0f, 6.0f, 5.0f),
    PEAK_PARTY("peak_party", "Peak Party", 7.0f, 8.5f, 8.0f),
    ONLY_BANGERS("only_bangers", "Only Bangers", 8.5f, 10.0f, 9.4f),
    WINDING_DOWN("winding_down", "Winding Down", 3.0f, 4.5f, 3.8f);

    companion object {
        fun fromId(id: String): TimeOfNight? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}

/**
 * Crowd size context affecting track familiarity and tolerance for underground tracks.
 */
enum class CrowdSize(
    val id: String,
    val label: String,
    val description: String,
    val mainstreamBias: Float // 0.0 = niche/underground fine, 1.0 = heavy mainstream hit bias
) {
    SMALL("small", "4–6", "Intimate / talk over", 0.2f),
    MEDIUM("medium", "10–20", "Party room", 0.6f),
    LARGE("large", "20+", "Packed dancefloor", 0.9f);

    companion object {
        fun fromId(id: String): CrowdSize? = entries.find { it.id.equals(id, ignoreCase = true) }
    }
}

/**
 * User inputs configuring the generated party set.
 */
data class PartyRoomConfig(
    val specialtyVibes: Set<SpecialtyVibe> = setOf(SpecialtyVibe.UNDERGROUND_DHH),
    val timeOfNight: TimeOfNight = TimeOfNight.PEAK_PARTY,
    val crowdSize: CrowdSize = CrowdSize.MEDIUM,
    val hookWindowSec: Long = 38L, // Default duration of hook play before mixing out
    val customPrompt: String = ""
)
