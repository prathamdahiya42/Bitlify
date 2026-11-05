package com.music.bitchord.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassIconButton
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassBackground
import com.music.bitchord.ui.theme.GlassTokens

/**
 * Fullscreen Glassmorphic Now Playing Screen.
 * Features a dynamic album-art backdrop, 32dp-radius artwork with glow, 4dp translucent seek bar,
 * 72dp tinted play/pause circle, 48dp glass control circles, and secondary action pills.
 */
@Composable
fun GlassNowPlayingScreen(
    title: String,
    artist: String,
    durationMs: Long,
    currentPositionMs: Long,
    isPlaying: Boolean,
    isShuffle: Boolean,
    isRepeat: Boolean,
    isLiked: Boolean,
    dominantColorPrimary: Color = GlassTokens.BlobHotPink,
    dominantColorSecondary: Color = GlassTokens.BlobLilac,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenLyrics: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    GlassBackground(
        dynamicArtworkKey = title,
        dynamicArtColors = dominantColorPrimary to dominantColorSecondary
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        if (delta > 0 || dragOffsetY > 0) {
                            dragOffsetY = (dragOffsetY + delta).coerceAtLeast(0f)
                            if (dragOffsetY > 160f) {
                                onCollapse()
                            }
                        }
                    },
                    onDragStopped = { dragOffsetY = 0f }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Drag handle & Collapse chevron
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color(0x66FFFFFF), RoundedCornerShape(2.dp))
                )

                // Chevron down
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable(onClick = onCollapse)
                )
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // 1. Large 32dp Rounded Album Artwork with Dominant Glow
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = dominantColorPrimary.copy(alpha = 0.45f),
                        spotColor = dominantColorPrimary.copy(alpha = 0.55f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(GlassTokens.TintPink)
                    .border(1.dp, Color(0x4DFFFFFF), RoundedCornerShape(32.dp))
            )

            Spacer(modifier = Modifier.weight(0.2f))

            // 2. Title & Artist Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = GlassTokens.TypeTitle.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = artist,
                    style = GlassTokens.TypeBody.copy(
                        color = GlassTokens.TextSecondary,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Seek Bar (Thin 4dp track with translucent white thumb)
            val progressFraction = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
            Slider(
                value = progressFraction,
                onValueChange = { frac -> onSeek((frac * durationMs).toLong()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0x33FFFFFF)
                )
            )

            // Timestamps row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMillis(currentPositionMs),
                    style = GlassTokens.TypeLabel.copy(color = GlassTokens.TextSecondary, fontSize = 11.sp)
                )
                Text(
                    text = formatMillis(durationMs),
                    style = GlassTokens.TypeLabel.copy(color = GlassTokens.TextSecondary, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle (48dp glass circle)
                GlassIconButton(
                    icon = Icons.Rounded.Shuffle,
                    onClick = onToggleShuffle,
                    size = 48.dp,
                    tintGradient = if (isShuffle) GlassTokens.TintPink else null,
                    iconTint = if (isShuffle) Color.White else GlassTokens.TextSecondary
                )

                // Skip Previous (48dp glass circle)
                GlassIconButton(
                    icon = Icons.Rounded.SkipPrevious,
                    onClick = onSkipPrevious,
                    size = 48.dp,
                    iconTint = Color.White
                )

                // Play / Pause (Large 72dp Tinted Glass Circle)
                GlassIconButton(
                    icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    onClick = onPlayPause,
                    size = 72.dp,
                    tintGradient = GlassTokens.TintPink,
                    iconTint = Color.White
                )

                // Skip Next (48dp glass circle)
                GlassIconButton(
                    icon = Icons.Rounded.SkipNext,
                    onClick = onSkipNext,
                    size = 48.dp,
                    iconTint = Color.White
                )

                // Repeat (48dp glass circle)
                GlassIconButton(
                    icon = Icons.Rounded.Repeat,
                    onClick = onToggleRepeat,
                    size = 48.dp,
                    tintGradient = if (isRepeat) GlassTokens.TintPink else null,
                    iconTint = if (isRepeat) Color.White else GlassTokens.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Secondary Action Pills (Like, Queue, Lyrics, Share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionPill(
                    icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    label = "Like",
                    isActive = isLiked,
                    onClick = onToggleLike
                )
                ActionPill(
                    icon = Icons.Rounded.QueueMusic,
                    label = "Queue",
                    onClick = onOpenQueue
                )
                ActionPill(
                    icon = Icons.Rounded.Lyrics,
                    label = "Lyrics",
                    onClick = onOpenLyrics
                )
                ActionPill(
                    icon = Icons.Rounded.Share,
                    label = "Share",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(GlassTokens.PillShape)
            .clickable(onClick = onClick)
    ) {
        GlassSurface(
            modifier = Modifier
                .height(38.dp)
                .padding(horizontal = 4.dp),
            shape = GlassTokens.PillShape,
            tintGradient = if (isActive) GlassTokens.TintPink else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) Color.White else GlassTokens.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = GlassTokens.TypeLabel.copy(
                        color = if (isActive) Color.White else GlassTokens.TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

private fun formatMillis(millis: Long): String {
    val totalSec = (millis / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
