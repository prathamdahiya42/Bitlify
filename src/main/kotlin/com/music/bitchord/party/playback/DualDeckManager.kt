package com.music.bitchord.party.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.bitchord.party.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Dual-deck orchestrator managing two ExoPlayer instances (Deck A & Deck B).
 * Implements silent background preloading, hook-drop entry, and equal-power crossfading.
 */
class DualDeckManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val primarySessionPlayer: ExoPlayer? = null // Can wrap existing app player as Deck A
) {
    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    // Deck A: Uses primary player if provided, or creates new instance
    val playerA: ExoPlayer = primarySessionPlayer ?: ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .build()

    // Deck B: Auxiliary background player for silent preloading and crossfading
    val playerB: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, false) // Deck A retains primary audio focus
        .build()

    private val crossfadeController = PartyCrossfadeController(
        crossfadeDurationMs = 1500L,
        coroutineScope = coroutineScope
    )

    private val _partyState = MutableStateFlow(PartyState())
    val partyState: StateFlow<PartyState> = _partyState.asStateFlow()

    private var monitorJob: Job? = null
    private var isPartyModeActive = false

    init {
        setupPlayerListeners(playerA, DeckId.DECK_A)
        setupPlayerListeners(playerB, DeckId.DECK_B)
    }

    private fun setupPlayerListeners(player: ExoPlayer, deckId: DeckId) {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                _partyState.update { state ->
                    if (deckId == DeckId.DECK_A) {
                        state.copy(deckA = state.deckA.copy(isBuffering = isBuffering))
                    } else {
                        state.copy(deckB = state.deckB.copy(isBuffering = isBuffering))
                    }
                }

                // If buffer hold was active and incoming deck is now ready, resume crossfade transition
                if (playbackState == Player.STATE_READY && crossfadeController.isBufferHolding.value) {
                    val incomingDeckId = _partyState.value.incomingDeck.deckId
                    if (deckId == incomingDeckId) {
                        crossfadeController.setBufferHold(false)
                        triggerCrossfadeTransition()
                    }
                }

                // Handle natural track completion (e.g. if Play Full was active)
                if (playbackState == Player.STATE_ENDED) {
                    onTrackEnded(deckId)
                }
            }
        })
    }

    /**
     * Starts Party Mode with a curated queue.
     * Starts Track 0 on Deck A directly at its hook, preloads Track 1 on Deck B.
     */
    fun startParty(cues: List<PartyCue>, startIndex: Int = 0) {
        if (cues.isEmpty()) return
        isPartyModeActive = true

        val initialCue = cues.getOrNull(startIndex) ?: cues.first()
        val nextCue = cues.getOrNull(startIndex + 1)

        _partyState.value = PartyState(
            isEnabled = true,
            activeDeckId = DeckId.DECK_A,
            currentCue = initialCue,
            nextCue = nextCue,
            queue = cues,
            queueIndex = startIndex,
            isPlayFullActive = false,
            deckA = DeckState(DeckId.DECK_A, DeckStatus.LIVE, initialCue, 1.0f),
            deckB = DeckState(DeckId.DECK_B, DeckStatus.IDLE, nextCue, 0.0f)
        )

        // Drop Track 1 directly into hook on Deck A
        loadCueIntoPlayer(playerA, initialCue, autoPlay = true, initialVolume = 1.0f)

        // Preload Track 2 in background on Deck B (muted)
        if (nextCue != null) {
            preloadNextTrack(playerB, nextCue, DeckId.DECK_B)
        }

        startPositionMonitor()
    }

    /**
     * Loads a cue into player, seeking straight to hookStartSec.
     */
    private fun loadCueIntoPlayer(
        player: ExoPlayer,
        cue: PartyCue,
        autoPlay: Boolean,
        initialVolume: Float
    ) {
        player.volume = initialVolume
        val mediaItem = cue.audioUrl?.let { MediaItem.fromUri(it) } ?: MediaItem.Builder()
            .setMediaId(cue.id)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        // Skip the intro: Drop directly at hook timestamp
        player.seekTo(cue.hookStartMs)
        player.playWhenReady = autoPlay
    }

    /**
     * Silently preloads an upcoming track into the background deck.
     */
    private fun preloadNextTrack(player: ExoPlayer, cue: PartyCue, deckId: DeckId) {
        player.volume = 0.0f
        val mediaItem = cue.audioUrl?.let { MediaItem.fromUri(it) } ?: MediaItem.Builder()
            .setMediaId(cue.id)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        // Cue ready at hookStartSec
        player.seekTo(cue.hookStartMs)
        player.playWhenReady = false

        _partyState.update { state ->
            if (deckId == DeckId.DECK_A) {
                state.copy(deckA = state.deckA.copy(status = DeckStatus.READY, cue = cue, volume = 0.0f))
            } else {
                state.copy(deckB = state.deckB.copy(status = DeckStatus.READY, cue = cue, volume = 0.0f))
            }
        }
    }

    /**
     * Monitors live track position, auto-triggers 1.5s crossfade at hookEndMs,
     * and updates DJ narrative banner.
     */
    private fun startPositionMonitor() {
        monitorJob?.cancel()
        monitorJob = coroutineScope.launch {
            while (isActive && isPartyModeActive) {
                val state = _partyState.value
                val livePlayer = if (state.activeDeckId == DeckId.DECK_A) playerA else playerB
                val currentPositionMs = livePlayer.currentPosition
                val currentCue = state.currentCue
                val nextCue = state.nextCue

                // Update deck position
                _partyState.update { current ->
                    val narrative = crossfadeController.generateDjNarrative(
                        currentCue = currentCue,
                        nextCue = nextCue,
                        currentPositionMs = currentPositionMs,
                        isPlayFull = current.isPlayFullActive,
                        isHolding = crossfadeController.isBufferHolding.value
                    )
                    if (current.activeDeckId == DeckId.DECK_A) {
                        current.copy(
                            transitionNarrative = narrative,
                            deckA = current.deckA.copy(positionMs = currentPositionMs)
                        )
                    } else {
                        current.copy(
                            transitionNarrative = narrative,
                            deckB = current.deckB.copy(positionMs = currentPositionMs)
                        )
                    }
                }

                // Check for Hook mix-out cut point
                if (currentCue != null && nextCue != null && !state.isCrossfading) {
                    val shouldCut = crossfadeController.shouldTriggerCutPoint(
                        currentPositionMs = currentPositionMs,
                        hookEndMs = currentCue.hookEndMs,
                        isPlayFullActive = state.isPlayFullActive
                    )

                    if (shouldCut) {
                        val incomingPlayer = if (state.activeDeckId == DeckId.DECK_A) playerB else playerA
                        // Buffer Hold Safety: If incoming track is still buffering, hold live track playing
                        if (incomingPlayer.playbackState == Player.STATE_BUFFERING) {
                            crossfadeController.setBufferHold(true)
                        } else {
                            crossfadeController.setBufferHold(false)
                            triggerCrossfadeTransition()
                        }
                    }
                }

                delay(50L)
            }
        }
    }

    /**
     * Triggers the 1.5s equal-power crossfade transition into the incoming deck.
     */
    private fun triggerCrossfadeTransition() {
        val state = _partyState.value
        val (outgoingPlayer, incomingPlayer) = if (state.activeDeckId == DeckId.DECK_A) {
            playerA to playerB
        } else {
            playerB to playerA
        }

        // Start incoming deck playing from hookStartMs
        incomingPlayer.playWhenReady = true

        _partyState.update { it.copy(isCrossfading = true) }

        crossfadeController.executeCrossfade(
            onVolumeUpdate = { incomingGain, outgoingGain ->
                incomingPlayer.volume = incomingGain
                outgoingPlayer.volume = outgoingGain
                _partyState.update { it.copy(crossfadeProgress = crossfadeController.crossfadeProgress.value) }
            },
            onComplete = {
                // Pause outgoing player
                outgoingPlayer.playWhenReady = false
                outgoingPlayer.volume = 0.0f

                // Toggle active deck
                val nextActiveDeckId = if (state.activeDeckId == DeckId.DECK_A) DeckId.DECK_B else DeckId.DECK_A
                val nextQueueIndex = state.queueIndex + 1
                val newCurrentCue = state.queue.getOrNull(nextQueueIndex)
                val newUpcomingCue = state.queue.getOrNull(nextQueueIndex + 1)

                _partyState.update { current ->
                    current.copy(
                        activeDeckId = nextActiveDeckId,
                        queueIndex = nextQueueIndex,
                        currentCue = newCurrentCue,
                        nextCue = newUpcomingCue,
                        isCrossfading = false,
                        crossfadeProgress = 0.0f,
                        isPlayFullActive = false
                    )
                }

                // Preload subsequent track on the now-idle deck
                if (newUpcomingCue != null) {
                    val idlePlayer = outgoingPlayer
                    val idleDeckId = state.activeDeckId
                    preloadNextTrack(idlePlayer, newUpcomingCue, idleDeckId)
                }
            }
        )
    }

    /**
     * SKIP Control: Triggers an immediate 1.5s smooth crossfade into the next song's hook.
     */
    fun skip() {
        val state = _partyState.value
        if (state.nextCue == null || state.isCrossfading) return
        triggerCrossfadeTransition()
    }

    /**
     * PLAY FULL Control: Bypasses the cut point to let user hear the whole song.
     */
    fun togglePlayFull() {
        _partyState.update { it.copy(isPlayFullActive = !it.isPlayFullActive) }
    }

    /**
     * SEEK Control: Seeks current player. Auto-enables Play Full if within 3s of cut point.
     */
    fun seekTo(positionMs: Long) {
        val state = _partyState.value
        val livePlayer = if (state.activeDeckId == DeckId.DECK_A) playerA else playerB
        val currentCue = state.currentCue

        if (currentCue != null) {
            if (crossfadeController.shouldAutoEnablePlayFullOnSeek(positionMs, currentCue.hookEndMs)) {
                _partyState.update { it.copy(isPlayFullActive = true) }
            }
        }
        livePlayer.seekTo(positionMs)
    }

    fun pause() {
        val state = _partyState.value
        val livePlayer = if (state.activeDeckId == DeckId.DECK_A) playerA else playerB
        livePlayer.playWhenReady = false
    }

    fun resume() {
        val state = _partyState.value
        val livePlayer = if (state.activeDeckId == DeckId.DECK_A) playerA else playerB
        livePlayer.playWhenReady = true
    }

    private fun onTrackEnded(deckId: DeckId) {
        val state = _partyState.value
        if (state.activeDeckId == deckId && state.nextCue != null) {
            triggerCrossfadeTransition()
        }
    }

    /**
     * Stops Party Mode cleanly and restores standard volume on Player A.
     */
    fun stopParty() {
        isPartyModeActive = false
        monitorJob?.cancel()
        crossfadeController.cancelCrossfade()

        playerB.stop()
        playerB.clearMediaItems()
        playerB.volume = 0.0f

        playerA.volume = 1.0f // Normal playback restored 100%

        _partyState.value = PartyState(isEnabled = false)
    }

    fun release() {
        stopParty()
        playerB.release()
        // Note: Only release playerA if we created it internally
        if (primarySessionPlayer == null) {
            playerA.release()
        }
    }
}
