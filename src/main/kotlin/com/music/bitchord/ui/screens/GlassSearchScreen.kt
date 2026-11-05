package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassSearchInput
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

data class GenreTile(val id: String, val name: String, val gradient: Brush)
data class SearchResultItem(val id: String, val title: String, val artist: String, val durationText: String)

/**
 * Redesigned Glassmorphic Search Screen.
 * Features 52dp glass search pill, 2-column tinted genre grid, and 60fps-optimized lite glass result cards.
 */
@Composable
fun GlassSearchScreen(
    onSongSelected: (SearchResultItem) -> Unit = {},
    onGenreSelected: (GenreTile) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val genres = remember {
        listOf(
            GenreTile("1", "Desi Hip-Hop", GlassTokens.TintPink),
            GenreTile("2", "Punjabi Pop", GlassTokens.TintOrange),
            GenreTile("3", "Drill & Rage", GlassTokens.TintMagenta),
            GenreTile("4", "Late Night Trap", GlassTokens.TintIndigo),
            GenreTile("5", "Indie Chill", GlassTokens.TintGreen),
            GenreTile("6", "Bollywood Remix", GlassTokens.TintPink)
        )
    }

    val mockResults = remember {
        listOf(
            SearchResultItem("s1", "Namastute", "Seedhe Maut", "3:12"),
            SearchResultItem("s2", "Makasam", "KR\$NA", "4:45"),
            SearchResultItem("s3", "Mirchi", "DIVINE ft. MC Altaf", "3:30"),
            SearchResultItem("s4", "Gumaan", "Young Stunners", "4:02"),
            SearchResultItem("s5", "Brown Munde", "AP Dhillon, Gurinder Gill", "4:28")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp)
    ) {
        // 1. 52dp Glass Search Pill
        GlassSearchInput(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Artists, songs, or lyrics...",
            leadingIcon = Icons.Rounded.Search,
            trailingIcon = Icons.Rounded.Clear,
            onTrailingIconClick = { searchQuery = "" }
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (searchQuery.isEmpty()) {
            // 2. Browse Genres & Moods (2-Column Grid)
            Text(
                text = "BROWSE ALL GENRES",
                style = GlassTokens.TypeLabel.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = GlassTokens.TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 180.dp)
            ) {
                items(genres.chunked(2)) { rowGenres ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowGenres.forEach { genre ->
                            GenreGridCard(
                                genre = genre,
                                onClick = { onGenreSelected(genre) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowGenres.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // 3. Search Results List (Lite Glass Cards for 60fps performance)
            Text(
                text = "TOP RESULTS",
                style = GlassTokens.TypeLabel.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = GlassTokens.TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 180.dp)
            ) {
                items(mockResults) { song ->
                    SearchResultRow(
                        song = song,
                        onClick = { onSongSelected(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreGridCard(
    genre: GenreTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(GlassTokens.TileShape)
            .clickable(onClick = onClick)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.TileShape,
            tintGradient = genre.gradient
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassTokens.TextContrastOverlay)
                    .padding(14.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = genre.name,
                    style = GlassTokens.TypeTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

/**
 * Search result row utilizing "lite" solid frosted glass without live blur for 60fps list scrolling.
 */
@Composable
private fun SearchResultRow(
    song: SearchResultItem,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        isLiteMode = true // Performance optimization: solid fill, no live blur
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 56dp Artwork Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassTokens.TintIndigo)
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = GlassTokens.TypeBody.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = GlassTokens.TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.artist} • ${song.durationText}",
                    style = GlassTokens.TypeLabel.copy(
                        color = GlassTokens.TextSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // More Options Icon
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "Options",
                tint = GlassTokens.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
