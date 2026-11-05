package com.music.bitchord.party.playback

import com.music.bitchord.party.model.PartyCue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Controller executing 1.5-second equal-power crossfades and managing
 * hook cut-point safety rules.
 */
class PartyCrossfadeController(
    private val crossfadeDurationMs: Long = 1500L,
    private val coroutineScope: CoroutineScope
) {
    private var crossfadeJob: Job? = null

    private val _isCrossfading = MutableStateFlow(false)
    val isCrossfading: StateFlow<Boolean> = _isCrossfading.asStateFlow()

    private val _crossfadeProgress = MutableStateFlow(0.0f)
    val crossfadeProgress: StateFlow<Float> = _crossfadeProgress.asStateFlow()

    private val _isBufferHolding = MutableStateFlow(false)
    val isBufferHolding: StateFlow<Boolean> = _isBufferHolding.asStateFlow()

    /**
     * Calculates sinusoidal equal-power volume curves:
     * - Incoming: sin(t * π / 2)
     * - Outgoing: cos(t * π / 2)
     *
     * This preserves constant acoustic power (V_in^2 + V_out^2 = 1.0)
     * preventing awkward perceived volume dips during transitions.
     */
    fun calculateEqualPowerGains(progress: Float): Pair<Float, Float> {
        val clamped = progress.coerceIn(0.0f, 1.0f)
        val angle = clamped * (PI.toFloat() / 2.0f)
        val incomingGain = sin(angle).coerceIn(0.0f, 1.0f)
        val outgoingGain = cos(angle).coerceIn(0.0f, 1.0f)
        return incomingGain to outgoingGain
    }

    /**
     * Executes a 1.5-second smooth crossfade between outgoing and incoming volume setters.
     */
    fun executeCrossfade(
        onVolumeUpdate: (incomingVolume: Float, outgoingVolume: Float) -> Unit,
        onComplete: () -> Unit
    ) {
        crossfadeJob?.cancel()
        _isCrossfading.value = true
        _isBufferHolding.value = false

        crossfadeJob = coroutineScope.launch {
            val startTime = System.currentTimeMillis()
            val stepIntervalMs = 20L

            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / crossfadeDurationMs.toFloat()).coerceIn(0.0f, 1.0f)
                _crossfadeProgress.value = progress

                val (incomingVol, outgoingVol) = calculateEqualPowerGains(progress)
                onVolumeUpdate(incomingVol, outgoingVol)

                if (progress >= 1.0f) {
                    break
                }
                delay(stepIntervalMs)
            }

            // Ensure exact final volumes
            onVolumeUpdate(1.0f, 0.0f)
            _isCrossfading.value = false
            _crossfadeProgress.value = 0.0f
            onComplete()
        }
    }

    /**
     * Cancels any running crossfade immediately and resets values.
     */
    fun cancelCrossfade() {
        crossfadeJob?.cancel()
        crossfadeJob = null
        _isCrossfading.value = false
        _crossfadeProgress.value = 0.0f
        _isBufferHolding.value = false
    }

    /**
     * Checks if current position reached the cut point.
     * Returns true if crossfade should start.
     */
    fun shouldTriggerCutPoint(
        currentPositionMs: Long,
        hookEndMs: Long,
        isPlayFullActive: Boolean
    ): Boolean {
        if (isPlayFullActive) return false
        val mixPointMs = hookEndMs - crossfadeDurationMs
        return currentPositionMs >= mixPointMs
    }

    /**
     * Safety check: If user seeks within 3 seconds of the cut point,
     * auto-enable Play Full so the track does not abruptly end.
     */
    fun shouldAutoEnablePlayFullOnSeek(
        seekPositionMs: Long,
        hookEndMs: Long
    ): Boolean {
        val deltaToCut = hookEndMs - seekPositionMs
        return deltaToCut in 0L..3000L
    }

    /**
     * Sets or releases buffer hold status when incoming track is buffering.
     */
    fun setBufferHold(holding: Boolean) {
        _isBufferHolding.value = holding
    }

    /**
     * Generates a live transition commentary for the DJ banner based on energy delta.
     */
    fun generateDjNarrative(
        currentCue: PartyCue?,
        nextCue: PartyCue?,
        currentPositionMs: Long,
        isPlayFull: Boolean,
        isHolding: Boolean
    ): String {
        if (currentCue == null) return "DJ Deck waiting for tracks"

        if (isHolding) {
            return "Holding the deck — locking in next groove 🎧"
        }

        if (isPlayFull) {
            return "Playing full track: letting '${currentCue.title}' run 🔥"
        }

        if (nextCue == null) {
            return "Final anthem rolling..."
        }

        val secondsLeft = ((currentCue.hookEndMs - currentPositionMs) / 1000L).coerceAtLeast(0L)
        val energyDelta = nextCue.energy - currentCue.energy

        return when {
            secondsLeft <= 3L -> {
                "Mixing into '${nextCue.title}'..."
            }
            energyDelta > 0.8f -> {
                "Cutting at ${formatTime(currentCue.hookEndMs)}, energy going up 🔥"
            }
            energyDelta < -0.8f -> {
                "Cutting at ${formatTime(currentCue.hookEndMs)}, bringing it down smooth 🌊"
            }
            else -> {
                "Holding the energy, dropping '${nextCue.title}' next"
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val totalSec = millis / 1000L
        val min = totalSec / 60L
        val sec = totalSec % 60L
        return "%d:%02d".format(min, sec)
    }
}
