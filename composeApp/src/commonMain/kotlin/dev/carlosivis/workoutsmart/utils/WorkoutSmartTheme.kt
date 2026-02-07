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
    primary = BluePrimary,
    onPrimary = WhitePure,
    primaryContainer = BluePrimary,
    onPrimaryContainer = WhitePure,
    secondary = OrangeAccent,
    onSecondary = WhitePure,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    error = ErrorRed,
    onError = WhitePure
)

val AppDarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = WhitePure,
    primaryContainer = BluePrimary,
    onPrimaryContainer = WhitePure,
    secondary = OrangeAccent,
    onSecondary = WhitePure,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    error = ErrorRed,
    onError = WhitePure
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
    large = RoundedCornerShape(dev.carlosivis.workoutsmart.utils.Shapes.Large)
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
