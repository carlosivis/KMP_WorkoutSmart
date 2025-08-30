package dev.carlosivis.workoutsmart.Utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


val AppLightColorScheme = lightColorScheme(
    primary = VioletRoyal,          // Cor principal para botões, ícones
    onPrimary = WhitePure,          // Cor do texto sobre a cor primária
    primaryContainer = LilacSoft,   // Variação mais clara da cor primária
    onPrimaryContainer = PurpleDeep, // Cor do texto sobre o primaryContainer
    secondary = MagentaAccent,      // Cor secundária, para acentos
    onSecondary = WhitePure,
    background = LavenderPale,      // Fundo principal
    onBackground = GreyNeutral,     // Cor do texto no fundo principal
    surface = WhitePure,            // Cor da superfície de cards, diálogos
    onSurface = GreyNeutral,        // Cor do texto na superfície
    error = Color(0xFFB00020),      // Cor para erros
    onError = WhitePure
)

val AppDarkColorScheme = darkColorScheme(
    primary = VioletRoyal,
    onPrimary = WhitePure,
    primaryContainer = PurpleDeep,
    onPrimaryContainer = LilacSoft,
    secondary = MagentaAccent,
    onSecondary = WhitePure,
    background = Color(0xFF121212), // Fundo escuro (pode ser um roxo bem escuro)
    onBackground = WhitePure,
    surface = Color(0xFF1E1E1E),
    onSurface = WhitePure,
    error = Color(0xFFCF6679),
    onError = Color(0xFF121212)
)
val typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = FontSizes.BodyMedium
    )
)
val shapes = Shapes(
    small = RoundedCornerShape(dev.carlosivis.workoutsmart.Utils.Shapes.Small),
    medium = RoundedCornerShape(dev.carlosivis.workoutsmart.Utils.Shapes.Medium),
    large = RoundedCornerShape(dev.carlosivis.workoutsmart.Utils.Shapes.Large)
)

@Composable
fun WorkoutsSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
