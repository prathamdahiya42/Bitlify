package com.music.bitchord.party.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.party.model.DeckId
import com.music.bitchord.party.model.PartyState

/**
 * Dynamic DJ Narrative Banner displayed above player controls.
 * Shows live commentary, active deck state, and crossfade progress.
 */
@Composable
fun DjNarrativeBanner(
    partyState: PartyState,
    modifier: Modifier = Modifier
) {
    if (!partyState.isEnabled) return

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF161616),
                        Color(0xFF221122),
                        Color(0xFF161616)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (partyState.isCrossfading) Color(0xFFFF0055) else Color(0xFF333333),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Top row: Deck status badges & crossfade indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Deck A badge
                DeckBadge(
                    deckName = "DECK A",
                    isLive = partyState.activeDeckId == DeckId.DECK_A,
                    pulseAlpha = pulseAlpha
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Deck B badge
                DeckBadge(
                    deckName = "DECK B",
                    isLive = partyState.activeDeckId == DeckId.DECK_B,
                    pulseAlpha = pulseAlpha
                )
            }

            // Mode indicator
            if (partyState.isCrossfading) {
                Text(
                    text = "CROSSFADING ${(partyState.crossfadeProgress * 100).toInt()}%",
                    color = Color(0xFFFF0055),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            } else if (partyState.isPlayFullActive) {
                Text(
                    text = "PLAY FULL ACTIVE",
                    color = Color(0xFF00FFCC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom row: Dynamic transition narrative
        Text(
            text = partyState.transitionNarrative,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1
        )
    }
}

@Composable
private fun DeckBadge(
    deckName: String,
    isLive: Boolean,
    pulseAlpha: Float
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isLive) Color(0xFFFF0055).copy(alpha = 0.25f) else Color(0xFF2A2A2A),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = if (isLive) Color(0xFFFF0055).copy(alpha = pulseAlpha) else Color(0xFF444444),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isLive) "$deckName [LIVE]" else "$deckName [READY]",
            color = if (isLive) Color(0xFFFF3377) else Color(0xFF888888),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
