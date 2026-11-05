# 🎧 Hook-Drop & Dual-Deck Party Mix Mode Integration Guide

This guide details how the **Hook-Drop & Party Mix** feature operates inside Bitlify / BitChord, guaranteeing **100% isolation** from standard playback.

---

## 📁 Package Structure

```
com/music/bitchord/party/
├── model/
│   ├── PartyCue.kt               // Track wrapper with hookStartSec, hookEndSec, energy, specialty vibes
│   ├── PartyVibe.kt              // Specialty vibes (DHH, Hustle, Legacy, Hardcore, Punjabi), TimeOfNight, CrowdSize
│   └── PartyState.kt             // Reactive state snapshot (Decks A/B, Live commentary, Crossfade %)
├── playback/
│   ├── PartyCrossfadeController.kt // 1.5s equal-power crossfade (sin/cos curves), buffer hold safety, seek protection
│   └── DualDeckManager.kt        // Dual ExoPlayer orchestrator, background preloading, hook seeking
├── generator/
│   └── PartySetGenerator.kt      // Deterministic vibe matching, energy curve progression, and pacing
├── ui/
│   ├── DjNarrativeBanner.kt      // Live transition commentary ("Cutting at 1:12, energy going up 🔥")
│   ├── PartyModeBottomSheet.kt   // Brutalist dark-mode bottom sheet with vibe chips & sliders
│   └── PartyModeButton.kt        // Quick launcher pill for player screen / top app bar
└── PartyModeCoordinator.kt       // Singleton bridge connecting everything cleanly to PlaybackService
```

---

## ⚡ Zero-Risk Integration Steps

### 1. In Your Compose Player Screen (`PlayerPane.kt` or similar):

Add the `PartyModeButton` and `PartyModeBottomSheet`:

```kotlin
val partyCoordinator = remember { PartyModeCoordinator.getInstance(context) }
val partyState by partyCoordinator.partyState.collectAsState()
var showPartySheet by remember { mutableStateOf(false) }

// Inside your player controls row:
PartyModeButton(
    partyState = partyState,
    onClick = { showPartySheet = true }
)

// Dynamic DJ commentary banner above seek bar when Party Mode is running:
if (partyState.isEnabled) {
    DjNarrativeBanner(partyState = partyState)
}

// Bottom sheet dialog:
if (showPartySheet) {
    PartyModeBottomSheet(
        partyState = partyState,
        onDismissRequest = { showPartySheet = false },
        onStartParty = { config ->
            // Pass current queue / library media items to curate:
            partyCoordinator.startPartyMode(
                catalogItems = currentQueueMediaItems,
                config = config
            )
        },
        onStopParty = {
            partyCoordinator.stopPartyMode()
        },
        onSkip = {
            partyCoordinator.skip()
        },
        onTogglePlayFull = {
            partyCoordinator.togglePlayFull()
        }
    )
}
```

---

## 🛡️ Absolute Safety Guarantees

1. **Party Mode OFF (`partyState.isEnabled == false`)**:
   - `DualDeckManager` auxiliary player (Deck B) is stopped, cleared, and muted.
   - Primary player operates 100% identically to the default BitChord audio pipeline without any interception or overhead.
2. **Party Mode ON (`partyState.isEnabled == true`)**:
   - Skips intros and seeks directly to `hookStartSec`.
   - Deck B preloads upcoming track silently in the background (`volume = 0.0f`).
   - Executes smooth 1.5s sinusoidal equal-power crossfades:
     $$\text{Volume}_{\text{in}}(t) = \sin\left(\frac{t}{1.5} \cdot \frac{\pi}{2}\right), \quad \text{Volume}_{\text{out}}(t) = \cos\left(\frac{t}{1.5} \cdot \frac{\pi}{2}\right)$$
   - **Buffer Hold Safety**: If Deck B is still buffering, Deck A holds playback — never cuts into silence.
   - **Seek Protection**: If user seeks within 3s of the cut point, `Play Full` auto-enables to prevent abrupt cutoff.
