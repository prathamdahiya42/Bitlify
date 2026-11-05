package com.music.bitchord.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.theme.GlassTokens

/**
 * Reusable frosted glass surface container.
 * Features translucent fill, 1px light border, soft top highlight, and optional lite mode.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = GlassTokens.CardShape,
    isLiteMode: Boolean = false,
    isDarkTheme: Boolean = true,
    tintGradient: Brush? = null,
    borderWidth: Dp = GlassTokens.GlassBorderThin,
    borderColor: Color = GlassTokens.GlassBorder,
    content: @Composable BoxScope.() -> Unit
) {
    val baseFill = when {
        isLiteMode -> GlassTokens.GlassLiteFill
        isDarkTheme -> GlassTokens.GlassFillDark
        else -> GlassTokens.GlassFillLight
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = GlassTokens.GlassShadow,
                spotColor = GlassTokens.GlassShadow
            )
            .clip(shape)
            .background(baseFill)
            .then(
                if (tintGradient != null) Modifier.background(tintGradient) else Modifier
            )
            .border(BorderStroke(borderWidth, borderColor), shape)
    ) {
        // Soft inner top highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GlassTokens.GlassInsetHighlight, Color.Transparent)
                    )
                )
        )

        content()
    }
}

/**
 * Interactive Glass Button with scale-to-0.97 and +8% fill opacity on press (120ms).
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    tintGradient: Brush? = if (isPrimary) GlassTokens.TintPink else null,
    height: Dp = 52.dp,
    shape: Shape = GlassTokens.PillShape,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = tween(120),
        label = "btnScale"
    )

    val pressOverlayAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.08f else 0.0f,
        animationSpec = tween(120),
        label = "btnPressAlpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            tintGradient = tintGradient
        ) {
            // Text contrast overlay + press feedback
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.10f + pressOverlayAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        style = GlassTokens.TypeBody.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

/**
 * 48dp circular glass icon button with pressed scale animation.
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = GlassTokens.MinTouchTarget,
    tintGradient: Brush? = null,
    iconTint: Color = GlassTokens.TextPrimary,
    shape: Shape = CircleShape
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(120),
        label = "iconBtnScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            tintGradient = tintGradient
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(size * 0.48f)
                )
            }
        }
    }
}

/**
 * 52dp Glass Pill Search Field with 60% white placeholder and brightened border on focus.
 */
@Composable
fun GlassSearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search songs, albums, artists...",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xCCFFFFFF) else GlassTokens.GlassBorder,
        animationSpec = tween(200),
        label = "searchBorderColor"
    )

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = GlassTokens.PillShape,
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = GlassTokens.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = GlassTokens.TypeBody.copy(color = Color(0x99FFFFFF)) // 60% white
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = GlassTokens.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }

            if (trailingIcon != null && query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = GlassTokens.TextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onTrailingIconClick?.invoke() }
                )
            }
        }
    }
}

/**
 * 140x88dp Tinted Glass Capsule (as in reference image 2 Quick Picks).
 */
@Composable
fun GlassCapsule(
    label: String,
    value: String,
    tintGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDashedPlaceholder: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(120),
        label = "capsuleScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(width = 140.dp, height = 88.dp)
            .clip(GlassTokens.CapsuleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        GlassSurface(
            modifier = Modifier.fillMaxSize(),
            shape = GlassTokens.CapsuleShape,
            tintGradient = if (isDashedPlaceholder) null else tintGradient,
            borderColor = if (isDashedPlaceholder) Color(0x66FFFFFF) else GlassTokens.GlassBorder
        ) {
            // 10% dark overlay for 4.5:1 contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GlassTokens.TextContrastOverlay)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Centered top label
                Text(
                    text = label.uppercase(),
                    style = GlassTokens.TypeLabel.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                // Large thin bottom value
                Text(
                    text = value,
                    style = GlassTokens.TypeTitle.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.BottomStart),
                    maxLines = 1
                )
            }
        }
    }
}
