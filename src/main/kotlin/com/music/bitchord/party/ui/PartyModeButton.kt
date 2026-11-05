package com.music.bitchord.party.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.party.model.PartyState

/**
 * Quick access button to trigger the Party Mode Bottom Sheet from the player screen or library.
 */
@Composable
fun PartyModeButton(
    partyState: PartyState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (partyState.isEnabled) Color(0xFFFF0055) else Color(0xFF33333C),
        label = "borderColor"
    )

    val bgColor by animateColorAsState(
        targetValue = if (partyState.isEnabled) Color(0xFF2E101E) else Color(0xFF16161B),
        label = "bgColor"
    )

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (partyState.isEnabled) "🔥" else "⚡",
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (partyState.isEnabled) "PARTY MIX LIVE" else "PARTY MODE",
            color = if (partyState.isEnabled) Color(0xFFFF0055) else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}
