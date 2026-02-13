package dev.carlosivis.workoutsmart.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BrandBlue = Color(0xFF0A84FF)
val BrandBlueContainerLight = Color(0xFFD6E9FF)
val BrandBlueContainerDark = Color(0xFF003D80)

val BrandOrange = Color(0xFFFF9F0A)
val BrandOrangeContainerLight = Color(0xFFFFE0B2)
val BrandOrangeContainerDark = Color(0xFF593300)

val BrandPurple = Color(0xFF6C63FF)

val ErrorRed = Color(0xFFFF3B30)

val WhitePure = Color(0xFFFFFFFF)
val BlackPure = Color(0xFF000000)

val Neutral50 = Color(0xFFF9FAFB)
val Neutral100 = Color(0xFFF3F4F6)
val Neutral200 = Color(0xFFE5E7EB)
val Neutral400 = Color(0xFF6B7280) // mais contraste
val Neutral700 = Color(0xFF374151)
val Neutral900 = Color(0xFF111827)

val NeutralDark900 = Color(0xFF121212)
val NeutralDark800 = Color(0xFF1F1F1F)
val NeutralDark700 = Color(0xFF2C2C2E)
val NeutralDarkGray = Color(0xFF9CA3AF)


val GoldColor = Color(0xFFFFD54F)
val GoldGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFF4CC).copy(alpha = 0.95f),
        Color(0xFFFFE082).copy(alpha = 0.65f),
        Color(0xFFFFC107).copy(alpha = 0.45f)
    )
)
val GoldGradientLinear = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFF4CC).copy(alpha = 0.95f),
        Color(0xFFFFE082).copy(alpha = 0.65f),
        Color(0xFFFFC107).copy(alpha = 0.45f)
    )
)


val SilverColor = Color(0xFFCFD8DC)
val SilverGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF5F7F8).copy(alpha = 0.99f),
        Color(0xFFCFD8DC).copy(alpha = 0.60f),
        Color(0xFF90A4AE).copy(alpha = 0.45f)
    )
)
val SilverGradientLinear = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF5F7F8).copy(alpha = 0.99f),
        Color(0xFFCFD8DC).copy(alpha = 0.60f),
        Color(0xFF90A4AE).copy(alpha = 0.45f)
    )
)

val BronzeColor = Color(0xFFD7A16E)
val BronzeGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFE0C2).copy(alpha = 0.95f),
        Color(0xFFD7A16E).copy(alpha = 0.60f),
        Color(0xFFB87333).copy(alpha = 0.45f)
    )
)
val BronzeGradientLinear = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFE0C2).copy(alpha = 0.95f),
        Color(0xFFD7A16E).copy(alpha = 0.60f),
        Color(0xFFB87333).copy(alpha = 0.45f)
    )
)


val DefaultRankColor = BrandPurple