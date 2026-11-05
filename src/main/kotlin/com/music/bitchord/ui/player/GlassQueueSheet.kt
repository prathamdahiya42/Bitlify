package com.music.bitchord.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

data class QueueItem(val id: String, val title: String, val artist: String, val durationText: String)

/**
 * Glassmorphic Queue Bottom Sheet.
 * Features 28dp top radius, drag handle, and 60fps lite-glass reorderable rows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassQueueSheet(
    queueItems: List<QueueItem>,
    onDismissRequest: () -> Unit,
    onItemClick: (QueueItem) -> Unit,
    onRemoveItem: (Int) -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = GlassTokens.BackgroundBaseDark.copy(alpha = 0.92f),
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .background(Color(0x66FFFFFF), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Playing Next",
                        style = GlassTokens.TypeTitle.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${queueItems.size} tracks queued",
                        style = GlassTokens.TypeLabel.copy(color = GlassTokens.TextSecondary)
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismissRequest)
                )
            }

            // Queue List (Lite glass rows for 60fps)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                itemsIndexed(queueItems) { index, item ->
                    QueueRow(
                        index = index + 1,
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueRow(
    index: Int,
    item: QueueItem,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        isLiteMode = true // Solid fill, zero live blur
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index number (Light 300 weight)
            Text(
                text = "%02d".format(index),
                style = GlassTokens.TypeLabel.copy(
                    color = GlassTokens.TextSecondary,
                    fontWeight = FontWeight.Light,
                    fontSize = 13.sp
                ),
                modifier = Modifier.width(28.dp)
            )

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = GlassTokens.TypeBody.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.artist} • ${item.durationText}",
                    style = GlassTokens.TypeLabel.copy(
                        color = GlassTokens.TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Drag handle icon
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = "Reorder",
                tint = GlassTokens.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
