package com.music.bitchord.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.music.bitchord.party.PartyModeCoordinator
import com.music.bitchord.party.ui.DjNarrativeBanner
import com.music.bitchord.party.ui.PartyModeBottomSheet
import com.music.bitchord.party.ui.PartyModeButton
import com.music.bitchord.ui.components.GlassBottomNav
import com.music.bitchord.ui.components.GlassNavTab
import com.music.bitchord.ui.player.GlassMiniPlayer
import com.music.bitchord.ui.player.GlassNowPlayingScreen
import com.music.bitchord.ui.player.GlassQueueSheet
import com.music.bitchord.ui.player.QueueItem
import com.music.bitchord.ui.screens.GlassHomeScreen
import com.music.bitchord.ui.screens.GlassLibraryScreen
import com.music.bitchord.ui.screens.GlassSearchScreen
import com.music.bitchord.ui.theme.GlassBackground
import com.music.bitchord.ui.utils.rememberGlassHaptics

/**
 * Root Application UI Host implementing the redesigned glassmorphic experience.
 * Manages fixed background, navigation tabs, floating mini player, and fullscreen Now Playing.
 */
@Composable
fun GlassAppHost(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = rememberGlassHaptics()

    // Navigation & Player State
    var currentTab by remember { mutableStateOf(GlassNavTab.HOME) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var isQueueSheetVisible by remember { mutableStateOf(false) }
    var isPartySheetVisible by remember { mutableStateOf(false) }

    // Mock/Live Playback Binding
    var isPlaying by remember { mutableStateOf(true) }
    var currentSongTitle by remember { mutableStateOf("Namastute") }
    var currentSongArtist by remember { mutableStateOf("Seedhe Maut") }
    var currentProgress by remember { mutableFloatStateOf(0.42f) }

    // Party Mode Coordinator Integration
    val partyCoordinator = remember { PartyModeCoordinator.getInstance(context) }
    val partyState by partyCoordinator.partyState.collectAsState()

    // 1. Root Fixed Background (Ambient Blobs + Grain)
    GlassBackground {
        Box(modifier = modifier.fillMaxSize()) {
            // 2. Active Screen Content
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(220),
                label = "screenCrossfade"
            ) { tab ->
                when (tab) {
                    GlassNavTab.HOME -> GlassHomeScreen(
                        onQuickPickClick = { /* Navigate */ },
                        onAlbumTileClick = { tile ->
                            currentSongTitle = tile.title
                            currentSongArtist = tile.subtitle
                            isPlaying = true
                        }
                    )
                    GlassNavTab.SEARCH -> GlassSearchScreen(
                        onSongSelected = { song ->
                            currentSongTitle = song.title
                            currentSongArtist = song.artist
                            isPlaying = true
                        }
                    )
                    GlassNavTab.LIBRARY -> GlassLibraryScreen(
                        onItemClick = { item ->
                            currentSongTitle = item.name
                            isPlaying = true
                        }
                    )
                    GlassNavTab.PROFILE -> GlassHomeScreen() // Can link to Profile / Settings
                }
            }

            // 3. Floating Mini Player & Bottom Navigation Column
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dynamic Party Banner (if Party Mode is active)
                if (partyState.isEnabled) {
                    DjNarrativeBanner(partyState = partyState)
                }

                // Floating Mini Player (64dp pill directly above nav)
                GlassMiniPlayer(
                    title = currentSongTitle,
                    artist = currentSongArtist,
                    isPlaying = isPlaying,
                    progress = currentProgress,
                    onPlayPause = {
                        isPlaying = !isPlaying
                        haptics.playPauseFeedback()
                    },
                    onSkip = {
                        haptics.lightClick()
                    },
                    onExpand = {
                        isNowPlayingExpanded = true
                    }
                )

                // Floating Bottom Nav (16dp margins from edges)
                GlassBottomNav(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        currentTab = tab
                        haptics.lightClick()
                    }
                )
            }

            // 4. Fullscreen Now Playing Overlay
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(250)) + fadeOut()
            ) {
                GlassNowPlayingScreen(
                    title = currentSongTitle,
                    artist = currentSongArtist,
                    durationMs = 210_000L,
                    currentPositionMs = (210_000L * currentProgress).toLong(),
                    isPlaying = isPlaying,
                    isShuffle = false,
                    isRepeat = false,
                    isLiked = true,
                    onPlayPause = {
                        isPlaying = !isPlaying
                        haptics.playPauseFeedback()
                    },
                    onSkipNext = { haptics.lightClick() },
                    onSkipPrevious = { haptics.lightClick() },
                    onSeek = { pos -> currentProgress = pos / 210_000f },
                    onToggleShuffle = { haptics.toggleFeedback() },
                    onToggleRepeat = { haptics.toggleFeedback() },
                    onToggleLike = { haptics.lightClick() },
                    onOpenQueue = { isQueueSheetVisible = true },
                    onOpenLyrics = {},
                    onCollapse = { isNowPlayingExpanded = false }
                )
            }

            // 5. Queue Bottom Sheet
            if (isQueueSheetVisible) {
                GlassQueueSheet(
                    queueItems = listOf(
                        QueueItem("1", "Namastute", "Seedhe Maut", "3:12"),
                        QueueItem("2", "Makasam", "KR\$NA", "4:45"),
                        QueueItem("3", "Brown Munde", "AP Dhillon", "4:28"),
                        QueueItem("4", "Gumaan", "Young Stunners", "4:02")
                    ),
                    onDismissRequest = { isQueueSheetVisible = false },
                    onItemClick = { item ->
                        currentSongTitle = item.title
                        currentSongArtist = item.artist
                        isQueueSheetVisible = false
                    }
                )
            }

            // 6. Party Mode Bottom Sheet
            if (isPartySheetVisible) {
                PartyModeBottomSheet(
                    partyState = partyState,
                    onDismissRequest = { isPartySheetVisible = false },
                    onStartParty = { config ->
                        partyCoordinator.startPartyMode(
                            catalogItems = emptyList(),
                            config = config
                        )
                    },
                    onStopParty = { partyCoordinator.stopPartyMode() },
                    onSkip = { partyCoordinator.skip() },
                    onTogglePlayFull = { partyCoordinator.togglePlayFull() }
                )
            }
        }
    }
}
