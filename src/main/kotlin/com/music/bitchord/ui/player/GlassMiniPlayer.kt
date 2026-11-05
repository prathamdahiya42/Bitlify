package com.music.bitchord.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassIconButton
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

/**
 * Floating 64dp glass pill mini player positioned above the bottom navigation bar.
 * Features 44dp album art, song title & artist, play/pause controls, and a thin progress line.
 */
@Composable
fun GlassMiniPlayer(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float, // 0.0f to 1.0f
    onPlayPause: () -> Unit,
    onSkip: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
    artworkComposable: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(64.dp)
            .clip(GlassTokens.PillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExpand
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.PillShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 44dp Album Artwork
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    if (artworkComposable != null) {
                        artworkComposable()
                    } else {
                        // Placeholder art gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GlassTokens.TintPink)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Artist
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = GlassTokens.TypeBody.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GlassTokens.TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = artist,
                        style = GlassTokens.TypeLabel.copy(
                            color = GlassTokens.TextSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play / Pause Glass Circle Button
                GlassIconButton(
                    icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    onClick = onPlayPause,
                    size = 40.dp,
                    tintGradient = GlassTokens.TintPink,
                    iconTint = Color.White
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Skip Next
                GlassIconButton(
                    icon = Icons.Rounded.SkipNext,
                    onClick = onSkip,
                    size = 36.dp,
                    iconTint = GlassTokens.TextPrimary
                )
            }

            // Thin progress line along the bottom edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .align(Alignment.BottomStart)
                    .background(Color(0x1AFFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Color.White)
                )
            }
        }
    }
}
