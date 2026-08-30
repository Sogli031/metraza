package com.metraza.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Primary - vibrant teal
val Primary = Color(0xFF4FE3BC)
val PrimaryDark = Color(0xFF005140)
val PrimaryLight = Color(0xFF8AF5D8)
val PrimaryGradientStart = Color(0xFF2BBE95)
val PrimaryGradientEnd = Color(0xFF56E8C1)

// Secondary - warm amber
val Secondary = Color(0xFFFFB95F)
val SecondaryDark = Color(0xFF643F00)

// Surface colors - blago teal tonirane
val Surface = Color(0xFF161C1A)
val SurfaceLight = Color(0xFF1F2624)
val SurfaceElevated = Color(0xFF2A3330)
val Background = Color(0xFF0B100F)
val BackgroundGradientStart = Color(0xFF0A0F0E)
val BackgroundGradientEnd = Color(0xFF12201C)

// Text colors
val TextPrimary = Color(0xFFEAF2EE)
val TextSecondary = Color(0xFFB9C6C0)
val TextMuted = Color(0xFF8A9793)

// Semantic colors
val ErrorColor = Color(0xFFFFB4AB)
val SuccessColor = Color(0xFF7DD992)
val WarningColor = Color(0xFFFFB95F)

// Border colors
val BorderColor = Color(0xFF3A4642)
val BorderFocused = Primary
val GlassBorder = Color(0xFF6A7A74)

// Shadow
val ShadowColor = Color(0xFF000000)
val PrimaryShadow = Primary.copy(alpha = 0.18f)

// Material constants
const val MATERIAL_PLACEHOLDER = "Izaberite materijal"

val MATERIAL_DISPLAY_NAMES = listOf(
    "Cu",
    "CuZn10",
    "CuZn15",
    "CuZn20",
    "CuZn30",
    "CuZn37"
)

val MATERIAL_DENSITIES = mapOf(
    "cu" to 8960,
    "cuzn10" to 8800,
    "10" to 8800,
    "cuzn15" to 8630,
    "15" to 8630,
    "cuzn20" to 8530,
    "20" to 8530,
    "cuzn30" to 8403,
    "30" to 8403,
    "cuzn37" to 8285,
    "37" to 8285
)

// Layout tokens
data class LayoutTokens(
    val isCompact: Boolean,
    val screenPaddingHorizontal: Dp,
    val screenPaddingVertical: Dp,
    val sectionSpacing: Dp,
    val cardPaddingHorizontal: Dp,
    val cardPaddingVertical: Dp,
    val cardRadius: Dp,
    val itemPaddingHorizontal: Dp,
    val itemPaddingVertical: Dp,
    val buttonMinHeight: Dp,
    val buttonContentPadding: PaddingValues,
    val buttonSpacing: Dp
)

@Composable
fun layoutTokens(): LayoutTokens {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 360

    return LayoutTokens(
        isCompact = isCompact,
        screenPaddingHorizontal = if (isCompact) 12.dp else 16.dp,
        screenPaddingVertical = if (isCompact) 14.dp else 18.dp,
        sectionSpacing = if (isCompact) 10.dp else 12.dp,
        cardPaddingHorizontal = if (isCompact) 14.dp else 18.dp,
        cardPaddingVertical = if (isCompact) 12.dp else 16.dp,
        cardRadius = 22.dp,
        itemPaddingHorizontal = if (isCompact) 10.dp else 12.dp,
        itemPaddingVertical = if (isCompact) 8.dp else 10.dp,
        buttonMinHeight = if (isCompact) 48.dp else 54.dp,
        buttonContentPadding = if (isCompact) {
            PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        } else {
            PaddingValues(horizontal = 18.dp, vertical = 10.dp)
        },
        buttonSpacing = if (isCompact) 8.dp else 10.dp
    )
}
