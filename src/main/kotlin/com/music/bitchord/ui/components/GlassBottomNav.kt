package com.music.bitchord.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.music.bitchord.ui.theme.GlassTokens

enum class GlassNavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Rounded.Home),
    SEARCH("Search", Icons.Rounded.Search),
    LIBRARY("Library", Icons.Rounded.VideoLibrary),
    PROFILE("Profile", Icons.Rounded.Person)
}

/**
 * Floating glass pill navigation bar with 16dp margins from edges.
 * Features an active indicator circle with a 200ms smooth transition.
 */
@Composable
fun GlassBottomNav(
    selectedTab: GlassNavTab,
    onTabSelected: (GlassNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(64.dp)
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.PillShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassNavTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab

                    val activeBgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0x33FFFFFF) else Color.Transparent,
                        animationSpec = tween(200),
                        label = "tabBgAnim"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) Color.White else GlassTokens.TextSecondary,
                        animationSpec = tween(200),
                        label = "tabTintAnim"
                    )

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(activeBgColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
