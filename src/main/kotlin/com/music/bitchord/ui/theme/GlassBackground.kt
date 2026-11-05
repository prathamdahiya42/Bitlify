package com.music.bitchord.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Global blurred colorful backdrop with subtle film-grain overlay.
 * Supports static plum/blobs mode and dynamic Now Playing album-art mode.
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    dynamicArtworkKey: String? = null,
    dynamicArtColors: Pair<Color, Color>? = null,
    scrollOffset: Float = 0f,
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColor = if (isDarkTheme) GlassTokens.BackgroundBaseDark else GlassTokens.BackgroundBaseLight

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        if (dynamicArtworkKey != null && dynamicArtColors != null) {
            // Dynamic Mode: Crossfade 400ms when track changes
            Crossfade(
                targetState = dynamicArtColors,
                animationSpec = tween(if (reducedMotion) 0 else 400),
                label = "dynamicArtBackdrop"
            ) { colors ->
                DynamicArtBackdrop(
                    primary = colors.first,
                    secondary = colors.second,
                    baseColor = baseColor
                )
            }
        } else {
            // Default Mode: 3 large blurred blobs with subtle 0.1x parallax
            StaticBlobsBackdrop(
                scrollOffset = if (reducedMotion) 0f else scrollOffset * 0.10f
            )
        }

        // Film-grain overlay (4-6% opacity)
        FilmGrainOverlay(modifier = Modifier.fillMaxSize())

        // Foreground app content
        content()
    }
}

/**
 * Draws the 3 ambient colorful blobs (hot pink, lilac, peach) with smooth radial gradients.
 */
@Composable
private fun StaticBlobsBackdrop(
    scrollOffset: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .blur(80.dp) // Blur 80-100px
    ) {
        val width = size.width
        val height = size.height

        // 1. Hot Pink Blob (Top-left / center)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BlobHotPink.copy(alpha = GlassTokens.BlobOpacity),
                    Color.Transparent
                ),
                center = Offset(width * 0.25f, height * 0.20f + scrollOffset),
                radius = width * 0.65f
            ),
            radius = width * 0.65f,
            center = Offset(width * 0.25f, height * 0.20f + scrollOffset)
        )

        // 2. Lilac Blob (Mid-right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BlobLilac.copy(alpha = GlassTokens.BlobOpacity),
                    Color.Transparent
                ),
                center = Offset(width * 0.85f, height * 0.45f + scrollOffset * 0.8f),
                radius = width * 0.70f
            ),
            radius = width * 0.70f,
            center = Offset(width * 0.85f, height * 0.45f + scrollOffset * 0.8f)
        )

        // 3. Peach Blob (Bottom-center / left)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlassTokens.BlobPeach.copy(alpha = GlassTokens.BlobOpacity),
                    Color.Transparent
                ),
                center = Offset(width * 0.40f, height * 0.85f + scrollOffset * 0.5f),
                radius = width * 0.60f
            ),
            radius = width * 0.60f,
            center = Offset(width * 0.40f, height * 0.85f + scrollOffset * 0.5f)
        )
    }
}

/**
 * Dynamic mode for Now Playing screen: blurred, 30% darkened dominant color wash.
 */
@Composable
private fun DynamicArtBackdrop(
    primary: Color,
    secondary: Color,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .blur(70.dp)
    ) {
        val width = size.width
        val height = size.height

        // Darkened 30% base wash
        drawRect(color = baseColor.copy(alpha = 0.55f))

        // Dominant primary bloom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = 0.48f), Color.Transparent),
                center = Offset(width * 0.5f, height * 0.35f),
                radius = width * 0.9f
            ),
            radius = width * 0.9f,
            center = Offset(width * 0.5f, height * 0.35f)
        )

        // Secondary accent bloom
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = 0.38f), Color.Transparent),
                center = Offset(width * 0.65f, height * 0.75f),
                radius = width * 0.8f
            ),
            radius = width * 0.8f,
            center = Offset(width * 0.65f, height * 0.75f)
        )
    }
}

/**
 * Lightweight procedural micro-noise film grain overlay (5% opacity).
 */
@Composable
fun FilmGrainOverlay(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .alpha(GlassTokens.FilmGrainOpacity)
    ) {
        val step = 14f
        val points = mutableListOf<Offset>()
        val rnd = Random(42)

        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                if (rnd.nextFloat() > 0.45f) {
                    points.add(Offset(x + rnd.nextFloat() * 4f, y + rnd.nextFloat() * 4f))
                }
                x += step
            }
            y += step
        }

        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = Color.White,
            strokeWidth = 1.2f
        )
    }
}
