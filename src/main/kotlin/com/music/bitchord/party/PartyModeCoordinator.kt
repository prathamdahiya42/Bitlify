package com.music.bitchord.party

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.music.bitchord.party.generator.PartySetGenerator
import com.music.bitchord.party.model.PartyCue
import com.music.bitchord.party.model.PartyRoomConfig
import com.music.bitchord.party.model.PartyState
import com.music.bitchord.party.playback.DualDeckManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

/**
 * Isolated Central Coordinator for Party & Hook-Drop Mode.
 *
 * GUARANTEE: Operates strictly as an isolated add-on. When [isEnabled] is false,
 * normal playback pipeline (ExoPlayer, InnerTube extractors, AudioCache) is 100% untouched.
 */
class PartyModeCoordinator(
    private val context: Context,
    private val primarySessionPlayer: ExoPlayer? = null,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    val dualDeckManager = DualDeckManager(
        context = context,
        coroutineScope = scope,
        primarySessionPlayer = primarySessionPlayer
    )

    private val setGenerator = PartySetGenerator()

    val partyState: StateFlow<PartyState> = dualDeckManager.partyState

    /**
     * Activates Party Mode:
     * 1. Converts current media items or library songs into PartyCues with hook timestamps.
     * 2. Curates and sequences tracks matching the selected room vibes and energy curve.
     * 3. Drops the first track immediately at its hook, preloads the next, and begins 1.5s crossfade orchestration.
     */
    fun startPartyMode(
        catalogItems: List<MediaItem>,
        config: PartyRoomConfig,
        durationsMap: Map<String, Long> = emptyMap()
    ) {
        val candidateCues = catalogItems.map { mediaItem ->
            val duration = durationsMap[mediaItem.mediaId] ?: 180_000L
            PartyCue.fromMediaItem(mediaItem, durationMs = duration)
        }

        val partySet = setGenerator.generateSet(
            catalog = candidateCues,
            config = config,
            targetTrackCount = 15
        )

        dualDeckManager.startParty(partySet)
    }

    /**
     * Overload accepting pre-constructed PartyCue items.
     */
    fun startPartyWithCues(
        cues: List<PartyCue>,
        config: PartyRoomConfig
    ) {
        val partySet = setGenerator.generateSet(
            catalog = cues,
            config = config,
            targetTrackCount = 15
        )

        dualDeckManager.startParty(partySet)
    }

    /**
     * Deactivates Party Mode cleanly and restores standard ExoPlayer volume to 1.0f.
     */
    fun stopPartyMode() {
        dualDeckManager.stopParty()
    }

    fun skip() {
        dualDeckManager.skip()
    }

    fun togglePlayFull() {
        dualDeckManager.togglePlayFull()
    }

    fun seekTo(positionMs: Long) {
        dualDeckManager.seekTo(positionMs)
    }

    fun pause() {
        dualDeckManager.pause()
    }

    fun resume() {
        dualDeckManager.resume()
    }

    fun release() {
        dualDeckManager.release()
    }

    companion object {
        @Volatile
        private var instance: PartyModeCoordinator? = null

        fun getInstance(
            context: Context,
            primaryPlayer: ExoPlayer? = null
        ): PartyModeCoordinator {
            return instance ?: synchronized(this) {
                instance ?: PartyModeCoordinator(context.applicationContext, primaryPlayer).also {
                    instance = it
                }
            }
        }
    }
}
