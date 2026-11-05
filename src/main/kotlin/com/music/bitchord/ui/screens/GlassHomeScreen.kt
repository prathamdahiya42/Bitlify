package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassCapsule
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

data class AlbumTileItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val gradient: Brush
)

/**
 * Redesigned Glassmorphic Home Screen.
 * Features a minimalist greeting header, 2x2 Quick Picks capsule grid (140x88dp),
 * and horizontal 24dp-radius album tile sections.
 */
@Composable
fun GlassHomeScreen(
    greeting: String = "Good evening",
    onQuickPickClick: (String) -> Unit = {},
    onAlbumTileClick: (AlbumTileItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 180.dp) // Clears mini player + bottom nav (160dp+)
    ) {
        // 1. Greeting Header (No heavy top bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greeting,
                    style = GlassTokens.TypeTitle.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassTokens.TextPrimary
                    )
                )
                Text(
                    text = "Ready to jump into your mix?",
                    style = GlassTokens.TypeLabel.copy(
                        color = GlassTokens.TextSecondary,
                        fontSize = 13.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Quick Picks (2x2 Capsule Grid like reference image 2)
        Text(
            text = "QUICK PICKS",
            style = GlassTokens.TypeLabel.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = GlassTokens.TextSecondary
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCapsule(
                    label = "Liked Songs",
                    value = "248 Tracks",
                    tintGradient = GlassTokens.TintPink,
                    onClick = { onQuickPickClick("liked") },
                    modifier = Modifier.weight(1f)
                )
                GlassCapsule(
                    label = "Recently Played",
                    value = "Last Night",
                    tintGradient = GlassTokens.TintIndigo,
                    onClick = { onQuickPickClick("recent") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCapsule(
                    label = "Downloads",
                    value = "Offline Mix",
                    tintGradient = GlassTokens.TintGreen,
                    onClick = { onQuickPickClick("downloads") },
                    modifier = Modifier.weight(1f)
                )
                GlassCapsule(
                    label = "New Playlist",
                    value = "+ Create",
                    tintGradient = GlassTokens.TintOrange,
                    isDashedPlaceholder = true,
                    onClick = { onQuickPickClick("create") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Made For You (Horizontal scrolling 24dp album tiles)
        HomeSectionRow(
            sectionTitle = "Made for You",
            items = listOf(
                AlbumTileItem("1", "DHH Cypher Wave", "Seedhe Maut, KR$NA", GlassTokens.TintOrange),
                AlbumTileItem("2", "Late Night Drive", "The Local Train, Lifafa", GlassTokens.TintIndigo),
                AlbumTileItem("3", "High-Energy Drill", "Prabh Deep, MC Stan", GlassTokens.TintPink),
                AlbumTileItem("4", "Desi Club Heat", "Badshah, AP Dhillon", GlassTokens.TintMagenta)
            ),
            onItemClick = onAlbumTileClick
        )

        Spacer(modifier = Modifier.height(26.dp))

        // 4. Trending Now
        HomeSectionRow(
            sectionTitle = "Trending Now",
            items = listOf(
                AlbumTileItem("5", "Legacy Stadium Anthems", "Diljit, AR Rahman", GlassTokens.TintGreen),
                AlbumTileItem("6", "Hustle Hard cypher", "Raftaar, Karma", GlassTokens.TintPink),
                AlbumTileItem("7", "Underground Gully", "Divine, Naezy", GlassTokens.TintIndigo)
            ),
            onItemClick = onAlbumTileClick
        )
    }
}

@Composable
private fun HomeSectionRow(
    sectionTitle: String,
    items: List<AlbumTileItem>,
    onItemClick: (AlbumTileItem) -> Unit
) {
    Column {
        Text(
            text = sectionTitle,
            style = GlassTokens.TypeTitle.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 12.dp)
        ) {
            items(items) { item ->
                AlbumTile(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

/**
 * 24dp-radius album tile with title at bottom-left and small play icon at bottom-right.
 */
@Composable
private fun AlbumTile(
    item: AlbumTileItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .clip(GlassTokens.TileShape)
            .clickable(onClick = onClick)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.TileShape,
            tintGradient = item.gradient
        ) {
            // Dark gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC000000))
                        )
                    )
                    .padding(14.dp)
            ) {
                // Title and artist at bottom-left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(end = 36.dp)
                ) {
                    Text(
                        text = item.title,
                        style = GlassTokens.TypeBody.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.subtitle,
                        style = GlassTokens.TypeLabel.copy(
                            color = GlassTokens.TextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Small play icon at bottom-right
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
