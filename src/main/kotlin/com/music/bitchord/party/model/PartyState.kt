package com.music.bitchord.party.model

enum class DeckId {
    DECK_A,
    DECK_B
}

enum class DeckStatus {
    IDLE,
    PRELOADING,
    READY,
    LIVE,
    CROSSFADING
}

data class DeckState(
    val deckId: DeckId,
    val status: DeckStatus = DeckStatus.IDLE,
    val cue: PartyCue? = null,
    val volume: Float = 1.0f,
    val positionMs: Long = 0L,
    val isBuffering: Boolean = false
)

/**
 * Live snapshot of Party Mode playback.
 */
data class PartyState(
    val isEnabled: Boolean = false,
    val activeDeckId: DeckId = DeckId.DECK_A,
    val deckA: DeckState = DeckState(DeckId.DECK_A),
    val deckB: DeckState = DeckState(DeckId.DECK_B),
    val currentCue: PartyCue? = null,
    val nextCue: PartyCue? = null,
    val isCrossfading: Boolean = false,
    val crossfadeProgress: Float = 0.0f, // 0.0f -> 1.0f
    val isPlayFullActive: Boolean = false,
    val transitionNarrative: String = "DJ Deck standing by",
    val queue: List<PartyCue> = emptyList(),
    val queueIndex: Int = 0
) {
    val liveDeck: DeckState get() = if (activeDeckId == DeckId.DECK_A) deckA else deckB
    val incomingDeck: DeckState get() = if (activeDeckId == DeckId.DECK_A) deckB else deckA
}
