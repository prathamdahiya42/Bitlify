package com.music.bitchord.party.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.party.model.*

/**
 * Brutalist Dark-Mode Bottom Sheet for configuring and controlling
 * Hook-Drop & Party Mix mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyModeBottomSheet(
    partyState: PartyState,
    onDismissRequest: () -> Unit,
    onStartParty: (PartyRoomConfig) -> Unit,
    onStopParty: () -> Unit,
    onSkip: () -> Unit,
    onTogglePlayFull: () -> Unit
) {
    var selectedVibes by remember {
        mutableStateOf(setOf(SpecialtyVibe.UNDERGROUND_DHH, SpecialtyVibe.HUSTLE))
    }
    var selectedTime by remember { mutableStateOf(TimeOfNight.PEAK_PARTY) }
    var selectedCrowd by remember { mutableStateOf(CrowdSize.MEDIUM) }
    var customPrompt by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color(0xFF0F0F12),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .background(Color(0xFF333338), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HOOK-DROP // PARTY MIX",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Zero intros • Instant drops • 1.5s equal-power mix",
                        color = Color(0xFF999999),
                        fontSize = 12.sp
                    )
                }

                if (partyState.isEnabled) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFF0055).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFFF0055), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE ON AIR",
                            color = Color(0xFFFF0055),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Dynamic DJ Narrative Banner if running
            if (partyState.isEnabled) {
                DjNarrativeBanner(partyState = partyState)
                Spacer(modifier = Modifier.height(14.dp))

                // Active DJ Controls (Skip, Play Full)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF222228),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF444450))
                    ) {
                        Text(
                            text = "SKIP TO NEXT HOOK ⏭️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onTogglePlayFull,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (partyState.isPlayFullActive) Color(0xFF00FFCC) else Color(0xFF222228),
                            contentColor = if (partyState.isPlayFullActive) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (partyState.isPlayFullActive) Color(0xFF00FFCC) else Color(0xFF444450))
                    ) {
                        Text(
                            text = if (partyState.isPlayFullActive) "PLAYING FULL 🔥" else "PLAY FULL TRACK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1. Specialty Vibes Section
            Text(
                text = "SPECIALTY VIBE",
                color = Color(0xFF888890),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecialtyVibe.entries.forEach { vibe ->
                    val isSelected = selectedVibes.contains(vibe)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) Color(0xFF221128) else Color(0xFF141418),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFFF0055) else Color(0xFF26262E),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedVibes = if (isSelected) {
                                    if (selectedVibes.size > 1) selectedVibes - vibe else selectedVibes
                                } else {
                                    selectedVibes + vibe
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = vibe.emoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = vibe.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = vibe.subtitle,
                                color = Color(0xFF8E8E98),
                                fontSize = 11.sp
                            )
                        }
                        if (isSelected) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFFFF0055),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Time of Night / Energy
            Text(
                text = "TIME OF NIGHT // ENERGY",
                color = Color(0xFF888890),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TimeOfNight.entries) { time ->
                    val isSelected = selectedTime == time
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFFFF0055) else Color(0xFF18181E),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, if (isSelected) Color(0xFFFF0055) else Color(0xFF33333E), RoundedCornerShape(8.dp))
                            .clickable { selectedTime = time }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${time.title} (${time.minEnergy}-${time.maxEnergy})",
                            color = if (isSelected) Color.White else Color(0xFFCCCCCC),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Crowd Size
            Text(
                text = "CROWD SIZE",
                color = Color(0xFF888890),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrowdSize.entries.forEach { crowd ->
                    val isSelected = selectedCrowd == crowd
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color(0xFF26102C) else Color(0xFF18181E),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, if (isSelected) Color(0xFFFF0055) else Color(0xFF33333E), RoundedCornerShape(8.dp))
                            .clickable { selectedCrowd = crowd }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = crowd.label,
                                color = if (isSelected) Color(0xFFFF0055) else Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = crowd.description,
                                color = Color(0xFF777780),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Main Action Button
            if (!partyState.isEnabled) {
                Button(
                    onClick = {
                        val config = PartyRoomConfig(
                            specialtyVibes = selectedVibes,
                            timeOfNight = selectedTime,
                            crowdSize = selectedCrowd,
                            customPrompt = customPrompt
                        )
                        onStartParty(config)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0055)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "APPROVE & START THE SHOW 💀",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Button(
                    onClick = onStopParty,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E1018)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF3355))
                ) {
                    Text(
                        text = "STOP PARTY MODE 🛑",
                        color = Color(0xFFFF3355),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
