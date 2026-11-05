package com.music.bitchord.party.generator

import com.music.bitchord.party.model.PartyCue
import com.music.bitchord.party.model.PartyRoomConfig
import com.music.bitchord.party.model.SpecialtyVibe
import kotlin.math.abs

/**
 * Deterministic setlist generator that curates, scores, and sequences
 * tracks to build a continuous party energy arc based on room context.
 */
class PartySetGenerator {

    /**
     * Compiles a curated party queue from candidate cues matching room vibe and energy curve.
     */
    fun generateSet(
        catalog: List<PartyCue>,
        config: PartyRoomConfig,
        targetTrackCount: Int = 12
    ): List<PartyCue> {
        if (catalog.isEmpty()) return emptyList()

        val selectedVibeIds = config.specialtyVibes.map { it.id }.toSet()
        val targetEnergy = config.timeOfNight.targetEnergy
        val promptWords = config.customPrompt.lowercase().split(" ").filter { it.isNotBlank() }

        // 1. Score each candidate
        val scoredCandidates = catalog.map { cue ->
            var score = 0.0f

            // Vibe match score
            if (selectedVibeIds.contains(cue.specialtyVibe)) {
                score += 25.0f
            }

            // Keyword match in tags/title/artist from prompt
            val cueText = "${cue.title} ${cue.artist} ${cue.tags.joinToString(" ")}".lowercase()
            for (word in promptWords) {
                if (cueText.contains(word)) {
                    score += 10.0f
                }
            }

            // Energy proximity penalty: -2.0 per unit difference from target energy
            val energyDiff = abs(cue.energy - targetEnergy)
            score -= (energyDiff * 2.5f)

            // Crowd size adjustment:
            // Large crowd (20+) favors high energy & familiar anthems
            if (config.crowdSize.mainstreamBias > 0.7f && cue.energy >= 8.0f) {
                score += 5.0f
            }

            cue to score
        }

        // 2. Filter & select top tracks
        val sortedPool = scoredCandidates
            .sortedByDescending { it.second }
            .map { it.first }
            .distinctBy { it.id }

        val pool = sortedPool.take(targetTrackCount.coerceAtMost(catalog.size))
        if (pool.isEmpty()) return catalog.take(targetTrackCount)

        // 3. Sequence into a continuous energy arc:
        // Warm start -> Rising energy -> Climax -> Sustained groove
        return sequenceEnergyArc(pool, targetEnergy)
    }

    /**
     * Sequences tracks into a cohesive DJ arc rather than random jumps.
     * Prevents abrupt energy drops (e.g. 9.5 -> 3.2).
     */
    private fun sequenceEnergyArc(tracks: List<PartyCue>, targetEnergy: Float): List<PartyCue> {
        if (tracks.size <= 2) return tracks

        val remaining = tracks.toMutableList()
        val sequenced = mutableListOf<PartyCue>()

        // 1. Pick a good starter: closest to (targetEnergy - 0.5) to build into the set
        val starter = remaining.minByOrNull { abs(it.energy - (targetEnergy - 0.5f)) } ?: remaining.first()
        sequenced.add(starter)
        remaining.remove(starter)

        // 2. Successively pick the track with lowest energy transition distance (max delta <= 1.8)
        while (remaining.isNotEmpty()) {
            val lastEnergy = sequenced.last().energy
            val nextBest = remaining.minByOrNull { candidate ->
                val stepDiff = abs(candidate.energy - lastEnergy)
                // Slight preference for upward or steady energy
                val directionalBias = if (candidate.energy >= lastEnergy) 0.0f else 0.4f
                stepDiff + directionalBias
            } ?: remaining.first()

            sequenced.add(nextBest)
            remaining.remove(nextBest)
        }

        return sequenced
    }
}
