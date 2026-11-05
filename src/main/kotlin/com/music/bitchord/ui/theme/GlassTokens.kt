package com.music.bitchord.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design Tokens for Bitlify Glassmorphism Style.
 * Adheres strictly to the visual direction: restrained frosted glass, 
 * deep plum base, ambient blurred blobs, and thin typography.
 */
object GlassTokens {

    // --- Backgrounds & Ambient Blobs ---
    val BackgroundBaseDark = Color(0xFF2A1228)   // Deep plum default
    val BackgroundBaseLight = Color(0xFFF2C4D2)  // Light theme base

    val BlobHotPink = Color(0xFFE85A8A)
    val BlobLilac = Color(0xFFB79BD6)
    val BlobPeach = Color(0xFFFFB38A)

    const val BlobOpacity = 0.40f  // 35% - 45%
    const val FilmGrainOpacity = 0.05f // 4% - 6%

    // --- Glass Surface (Default) ---
    val GlassFillDark = Color(1f, 1f, 1f, 0.10f)
    val GlassFillLight = Color(1f, 1f, 1f, 0.14f)
    val GlassBorder = Color(1f, 1f, 1f, 0.35f)
    val GlassBorderThin = 1.dp
    val GlassInsetHighlight = Color(1f, 1f, 1f, 0.35f)
    val GlassShadow = Color(0f, 0f, 0f, 0.08f)
    val GlassBlurRadius = 18.dp

    // Lite glass for long scrolling lists (Performance optimization: solid, no live blur)
    val GlassLiteFill = Color(1f, 1f, 1f, 0.18f)
    val GlassFallbackFill = Color(1f, 1f, 1f, 0.30f)

    // --- Tinted Glass Gradients ---
    val TintPink = Brush.verticalGradient(
        colors = listOf(
            Color(0x99FF96BE), // rgba(255,150,190,.60)
            Color(0xD9E63C78)  // rgba(230,60,120,.85)
        )
    )

    val TintGreen = Brush.verticalGradient(
        colors = listOf(
            Color(0x8CE6E68C), // rgba(130,230,130,.55)
            Color(0xCC146E28)  // rgba(20,110,40,.80)
        )
    )

    val TintIndigo = Brush.verticalGradient(
        colors = listOf(
            Color(0x8C788CFF), // rgba(120,140,255,.55)
            Color(0xD919236E)  // rgba(25,35,110,.85)
        )
    )

    val TintOrange = Brush.verticalGradient(
        colors = listOf(
            Color(0x99FFAA64), // rgba(255,170,100,.60)
            Color(0xD9E65A28)  // rgba(230,90,40,.85)
        )
    )

    val TintMagenta = Brush.verticalGradient(
        colors = listOf(
            Color(0xA6EB5AE6), // rgba(235,90,230,.65)
            Color(0xD93C0F32)  // rgba(60,15,50,.85)
        )
    )

    // Contrast protection: 10% dark overlay under text on tinted surfaces
    val TextContrastOverlay = Color(0f, 0f, 0f, 0.10f)

    // --- Text & Typography ---
    val TextPrimary = Color(1f, 1f, 1f, 0.95f)
    val TextSecondary = Color(1f, 1f, 1f, 0.65f)

    val TypeLabel = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 12.sp,
        color = TextSecondary
    )

    val TypeBody = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal, // 400
        fontSize = 14.sp,
        color = TextPrimary
    )

    val TypeTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 20.sp,
        color = TextPrimary
    )

    val TypeBigNumber = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light, // 300
        fontSize = 38.sp,
        color = TextPrimary
    )

    // --- Shapes ---
    val CardShape = RoundedCornerShape(28.dp)
    val TileShape = RoundedCornerShape(24.dp)
    val PillShape = RoundedCornerShape(999.dp)
    val CapsuleShape = RoundedCornerShape(24.dp)

    val MinTouchTarget = 48.dp
}
