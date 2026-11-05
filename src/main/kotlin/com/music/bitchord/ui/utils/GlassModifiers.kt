package com.music.bitchord.ui.utils

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.music.bitchord.ui.components.GlassSurface
import com.music.bitchord.ui.theme.GlassTokens

/**
 * Applies a soft gradient fade mask on the top and bottom edges of scrollable areas.
 */
fun Modifier.scrollFadeMask(
    topFadeHeight: Dp = 20.dp,
    bottomFadeHeight: Dp = 30.dp
): Modifier = this.graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val height = size.height
        val width = size.width

        // Top fade
        if (topFadeHeight > 0.dp) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topFadeHeight.toPx()
                ),
                blendMode = BlendMode.DstIn
            )
        }

        // Bottom fade
        if (bottomFadeHeight > 0.dp) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = height - bottomFadeHeight.toPx(),
                    endY = height
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }

/**
 * Sticky glass header that only fades in (blur 14px) after 16px of scroll offset.
 */
@Composable
fun StickyGlassHeader(
    scrollOffsetPx: Float,
    thresholdPx: Float = 16f,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (scrollOffsetPx > thresholdPx) 1.0f else 0.0f,
        animationSpec = tween(200),
        label = "headerAlpha"
    )

    if (alpha > 0.01f) {
        GlassSurface(
            modifier = modifier
                .fillMaxWidth()
                .alpha(alpha)
                .statusBarsPadding()
                .height(56.dp),
            shape = GlassTokens.PillShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                content = content
            )
        }
    }
}

/**
 * Standardized lightweight haptic feedback utility.
 */
class GlassHaptics(private val view: View) {
    fun lightClick() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun toggleFeedback() {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun playPauseFeedback() {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }
}

@Composable
fun rememberGlassHaptics(): GlassHaptics {
    val view = LocalView.current
    return GlassHaptics(view)
}
