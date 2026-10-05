package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Navy Blue Palette (Primary, Branding, Headers, Navigation)
val NavyDark = Color(0xFF071426)
val NavyPrimary = Color(0xFF0B213F)
val NavyMedium = Color(0xFF132F54)
val NavyLight = Color(0xFF1C4273)
val NavySurface = Color(0xFF142B47)
val NavyContainer = Color(0xFFE0EBF7)
val OnNavyContainer = Color(0xFF081C33)

// Orange & Coral Palette (Actions, Primary Buttons, Highlights)
val OrangePrimary = Color(0xFFFF5E00)
val OrangeDark = Color(0xFFD94800)
val OrangeLight = Color(0xFFFFF2E6)
val OrangeSoft = Color(0xFFFFE8D6)
val OrangeContainer = Color(0xFFFFE0C2)
val OnOrangeContainer = Color(0xFF6B2600)
val CoralAccent = Color(0xFFFF7043)

// Gold & Amber Accents
val AmberGold = Color(0xFFFFB300)
val StarYellow = Color(0xFFFBBF24)

// Neutral & Background
val BackgroundLight = Color(0xFFF4F6F9)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEEF2F6)
val SurfaceBorder = Color(0xFFE2E8F0)
val SurfaceCard = Color(0xFFFFFFFF)

// Dark Theme Variants
val BackgroundDark = Color(0xFF0A0F18)
val SurfaceDark = Color(0xFF111827)
val SurfaceVariantDark = Color(0xFF1F2937)

// Text Colors
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF475569)
val TextMuted = Color(0xFF94A3B8)
val TextOnDark = Color(0xFFF8FAFC)

// Status & Indicators
val SuccessGreen = Color(0xFF059669)
val SuccessGreenLight = Color(0xFFD1FAE5)
val DiscountRed = Color(0xFFDC2626)
val DiscountRedLight = Color(0xFFFEE2E2)
val PendingOrange = Color(0xFFEA580C)
val ShippedBlue = Color(0xFF2563EB)
val MintGreen = Color(0xFF00BFA5)
val MintSoft = Color(0xFFE0F7F4)

// Gradient Brushes for rich visual mix
val NavyHeaderGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF071426), Color(0xFF0F2B52), Color(0xFF183D70))
)

val OrangeActionGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF5E00), Color(0xFFFF7A1A), Color(0xFFFF9436))
)

val FlashDealGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF831843), Color(0xFF9D174D), Color(0xFFBE185D))
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0x08FF5E00))
)
