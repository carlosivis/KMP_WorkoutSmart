package dev.carlosivis.workoutsmart.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.carlosivis.workoutsmart.repository.ThemeMode

val AppLightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = BlackPure,
    primaryContainer = BrandBlueContainerLight,
    onPrimaryContainer = BrandBlueContainerDark,

    secondary = BrandOrange,
    onSecondary = BlackPure,
    secondaryContainer = BrandOrangeContainerLight,
    onSecondaryContainer = BrandOrangeContainerDark,

    tertiary = BrandPurple,
    onTertiary = BlackPure,

    background = Neutral50,
    onBackground = Neutral900,

    surface = WhitePure,
    onSurface = Neutral900,

    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral900.copy(alpha = 0.8f),

    outline = Neutral200,

    error = ErrorRed,
    onError = WhitePure
)

val AppDarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = WhitePure,
    primaryContainer = BrandBlueContainerDark,
    onPrimaryContainer = BrandBlueContainerLight,

    secondary = BrandOrange,
    onSecondary = WhitePure,
    secondaryContainer = BrandOrangeContainerDark,
    onSecondaryContainer = BrandOrangeContainerLight,

    tertiary = BrandPurple,
    onTertiary = WhitePure,

    background = NeutralDark900,
    onBackground = WhitePure,

    surface = NeutralDark800,
    onSurface = WhitePure,

    surfaceVariant = NeutralDark700,
    onSurfaceVariant = NeutralDarkGray,
    outline = NeutralDark700,

    error = ErrorRed,
    onError = BlackPure
)

val typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = FontSizes.BodyMedium
    )
)

val shapes = Shapes(
    small = RoundedCornerShape(dev.carlosivis.workoutsmart.utils.Shapes.Small),
    medium = RoundedCornerShape(dev.carlosivis.workoutsmart.utils.Shapes.Medium),
    large = RoundedCornerShape(dev.carlosivis.workoutsmart.utils.Shapes.Large),
    extraLarge = RoundedCornerShape(dev.carlosivis.workoutsmart.utils.Shapes.ExtraLarge)
)

@Composable
fun WorkoutsSmartTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}