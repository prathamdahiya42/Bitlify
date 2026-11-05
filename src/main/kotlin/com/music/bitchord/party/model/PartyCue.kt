package com.music.bitchord.party.model

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

/**
 * Enhanced playback metadata wrapper for Party / Hook-Drop mode.
 * Contains hook cut points, energy ratings, and vibe classifications.
 */
data class PartyCue(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val hookStartSec: Long,
    val hookEndSec: Long,
    val energy: Float, // 1.0f (ambient) to 10.0f (maximum riot)
    val specialtyVibe: String, // e.g. "underground_dhh", "hustle", "hardcore"
    val thumbnailUrl: String? = null,
    val audioUrl: String? = null,
    val playFull: Boolean = false,
    val tags: List<String> = emptyList()
) {
    val hookStartMs: Long get() = hookStartSec * 1000L
    val hookEndMs: Long get() = hookEndSec * 1000L
    val hookWindowMs: Long get() = (hookEndSec - hookStartSec).coerceAtLeast(15L) * 1000L

    companion object {
        const val DEFAULT_FALLBACK_HOOK_WINDOW_SEC = 35L

        /**
         * Robust fallback heuristic for tracks without pre-tagged hook timestamps.
         * Default: Hook drops at 25% of total song duration, plays for 35s.
         */
        fun calculateFallbackHook(durationMs: Long): Pair<Long, Long> {
            val totalSec = if (durationMs > 0) durationMs / 1000L else 180L
            // If track is very short (< 45s), play from start to end
            if (totalSec <= 45L) {
                return 0L to totalSec
            }
            // 25% into track is usually where verse 1 ends and hook 1 drops
            val hookStart = (totalSec * 0.25f).toLong().coerceIn(15L, 60L)
            val hookEnd = (hookStart + DEFAULT_FALLBACK_HOOK_WINDOW_SEC).coerceAtMost(totalSec - 5L)
            return hookStart to hookEnd
        }

        /**
         * Wraps an AndroidX MediaItem into a PartyCue.
         */
        fun fromMediaItem(
            mediaItem: MediaItem,
            durationMs: Long = 180_000L,
            explicitEnergy: Float? = null,
            explicitVibe: String? = null
        ): PartyCue {
            val id = mediaItem.mediaId
            val metadata = mediaItem.mediaMetadata
            val title = metadata.title?.toString() ?: "Unknown Title"
            val artist = metadata.artist?.toString() ?: "Unknown Artist"
            val thumb = metadata.artworkUri?.toString()
            val (startSec, endSec) = calculateFallbackHook(durationMs)

            // Estimate vibe from title/artist tags if not explicitly provided
            val vibe = explicitVibe ?: inferVibeFromText("$title $artist")

            return PartyCue(
                id = id,
                title = title,
                artist = artist,
                durationMs = durationMs,
                hookStartSec = startSec,
                hookEndSec = endSec,
                energy = explicitEnergy ?: inferEnergyFromVibe(vibe),
                specialtyVibe = vibe,
                thumbnailUrl = thumb,
                audioUrl = mediaItem.localConfiguration?.uri?.toString()
            )
        }

        /**
         * Infers likely vibe from keywords in title or artist.
         */
        fun inferVibeFromText(text: String): String {
            val lower = text.lowercase()
            return when {
                SpecialtyVibe.UNDERGROUND_DHH.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.UNDERGROUND_DHH.id
                SpecialtyVibe.HARDCORE.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.HARDCORE.id
                SpecialtyVibe.PUNJABI_PARTY.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.PUNJABI_PARTY.id
                SpecialtyVibe.HUSTLE.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.HUSTLE.id
                SpecialtyVibe.DESI_DANCE.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.DESI_DANCE.id
                SpecialtyVibe.LEGACY_SHOW.associatedKeywords.any { lower.contains(it) } ->
                    SpecialtyVibe.LEGACY_SHOW.id
                else -> SpecialtyVibe.UNDERGROUND_DHH.id
            }
        }

        private fun inferEnergyFromVibe(vibeId: String): Float {
            return SpecialtyVibe.fromId(vibeId)?.defaultEnergy ?: 7.5f
        }
    }
}
