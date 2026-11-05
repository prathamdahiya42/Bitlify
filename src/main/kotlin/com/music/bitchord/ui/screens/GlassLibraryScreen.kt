package com.music.bitchord.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

enum class LibrarySectionType(val title: String, val icon: ImageVector) {
    PLAYLISTS("Playlists", Icons.Rounded.QueueMusic),
    LIKED_SONGS("Liked Songs", Icons.Rounded.Favorite),
    DOWNLOADS("Downloads", Icons.Rounded.Download),
    ALBUMS("Albums", Icons.Rounded.Album),
    ARTISTS("Artists", Icons.Rounded.Person)
}

data class LibraryItemData(
    val id: String,
    val name: String,
    val countText: String,
    val icon: ImageVector,
    val gradient: Brush
)

/**
 * Redesigned Glassmorphic Library Accordion (inspired by Reference Image 1).
 * Features 64dp collapsed pills that expand into 28dp glass cards
 * with 2-column tinted tiles, 280ms ease-out transitions, and single-section expansion.
 */
@Composable
fun GlassLibraryScreen(
    onItemClick: (LibraryItemData) -> Unit = {},
    onCreateNewClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expandedSection by remember { mutableStateOf<LibrarySectionType?>(LibrarySectionType.PLAYLISTS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 180.dp)
    ) {
        Text(
            text = "Your Library",
            style = GlassTokens.TypeTitle.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Playlists, saved tracks, and downloads",
            style = GlassTokens.TypeLabel.copy(
                color = GlassTokens.TextSecondary,
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Accordion Sections: Only one expanded at a time
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            LibrarySectionType.entries.forEach { section ->
                val isExpanded = expandedSection == section
                AccordionSectionCard(
                    section = section,
                    isExpanded = isExpanded,
                    onToggle = {
                        expandedSection = if (isExpanded) null else section
                    },
                    onItemClick = onItemClick,
                    onCreateNewClick = onCreateNewClick
                )
            }
        }
    }
}

@Composable
private fun AccordionSectionCard(
    section: LibrarySectionType,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onItemClick: (LibraryItemData) -> Unit,
    onCreateNewClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f, // "+" rotates into "x / -"
        animationSpec = tween(280),
        label = "plusRotate"
    )

    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        shape = if (isExpanded) GlassTokens.CardShape else GlassTokens.PillShape
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: 64dp tall row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle
                    )
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x26FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = section.title,
                        style = GlassTokens.TypeTitle.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Rotating "+" / "-" trigger icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x1FFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Expand",
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(rotation)
                    )
                }
            }

            // Expanded Content: 2-column grid of tinted tiles
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(240)) + fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val mockItems = listOf(
                        LibraryItemData("1", "DHH Bangers", "42 songs", Icons.Rounded.MusicNote, GlassTokens.TintPink),
                        LibraryItemData("2", "Workout Trap", "28 songs", Icons.Rounded.FitnessCenter, GlassTokens.TintGreen),
                        LibraryItemData("3", "Late Night Vibes", "64 songs", Icons.Rounded.NightsStay, GlassTokens.TintIndigo),
                        LibraryItemData("4", "Desi Hip-Hop Classics", "50 songs", Icons.Rounded.Mic, GlassTokens.TintOrange)
                    )

                    // 2-Column Grid with 12dp gap
                    for (i in mockItems.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LibraryGridTile(
                                item = mockItems[i],
                                onClick = { onItemClick(mockItems[i]) },
                                modifier = Modifier.weight(1f)
                            )
                            if (i + 1 < mockItems.size) {
                                LibraryGridTile(
                                    item = mockItems[i + 1],
                                    onClick = { onItemClick(mockItems[i + 1]) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // "Create New" placeholder tile
                    CreateNewPlaceholderTile(onClick = onCreateNewClick)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

/**
 * 2-column tinted tile with centered 40% icon, bottom-left name, and bottom-right more icon.
 */
@Composable
private fun LibraryGridTile(
    item: LibraryItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(GlassTokens.TileShape)
            .clickable(onClick = onClick)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.TileShape,
            tintGradient = item.gradient
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassTokens.TextContrastOverlay)
                    .padding(12.dp)
            ) {
                // Centered icon at 40% opacity
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.40f),
                    modifier = Modifier
                        .size(46.dp)
                        .align(Alignment.Center)
                )

                // Name and song count at bottom-left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(end = 28.dp)
                ) {
                    Text(
                        text = item.name,
                        style = GlassTokens.TypeBody.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.countText,
                        style = GlassTokens.TypeLabel.copy(
                            color = GlassTokens.TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                // Action icon (more) at bottom-right
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Options",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun CreateNewPlaceholderTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(GlassTokens.PillShape)
            .clickable(onClick = onClick)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.PillShape,
            borderColor = Color(0x66FFFFFF)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create new",
                    style = GlassTokens.TypeBody.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }
        }
    }
}
