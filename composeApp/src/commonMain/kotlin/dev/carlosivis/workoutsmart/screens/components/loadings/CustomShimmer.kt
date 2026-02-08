package dev.carlosivis.workoutsmart.screens.components.loadings

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import dev.carlosivis.workoutsmart.utils.Shapes

fun Modifier.placeholder(
    visible: Boolean,
    highlight: PlaceholderHighlight? = null,
    shape: Shape = RoundedCornerShape(Shapes.ExtraLarge)
): Modifier = composed {

    if (!visible || highlight !is PlaceholderHighlight.Shimmer) {
        return@composed this
    }

    val progress by rememberGlobalShimmerProgress(
        highlight.animationDurationMillis
    )

    shimmer(
        progress = progress,
        shimmerWidthFraction = highlight.shimmerWidthFraction,
        shape = shape
    )
}

// ------------------------------------------------------------
// Placeholder Highlight — estilo accompanist
// ------------------------------------------------------------

sealed interface PlaceholderHighlight {

    data class Shimmer(
        val animationDurationMillis: Int = 1100,
        val shimmerWidthFraction: Float = 0.6f
    ) : PlaceholderHighlight

    companion object {
        fun shimmer(
            animationDurationMillis: Int = 1100,
            shimmerWidthFraction: Float = 0.6f
        ): PlaceholderHighlight = Shimmer(
            animationDurationMillis,
            shimmerWidthFraction
        )
    }
}

@Composable
fun rememberGlobalShimmerProgress(
    durationMillis: Int = 1100
): State<Float> {
    val transition = rememberInfiniteTransition(label = "globalShimmer")

    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
}

private fun Modifier.shimmer(
    progress: Float,
    shimmerWidthFraction: Float,
    shape: Shape
): Modifier = drawWithCache {

    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }

    onDrawWithContent {

        val shimmerWidth = size.width * shimmerWidthFraction
        val startX = (size.width + shimmerWidth) * progress - shimmerWidth

        val brush = Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.35f),
                Color.LightGray.copy(alpha = 0.15f),
                Color.LightGray.copy(alpha = 0.35f)
            ),
            start = Offset(startX, 0f),
            end = Offset(startX + shimmerWidth, size.height)
        )

        clipPath(path) {
            drawRect(brush = brush, size = size)
        }
    }
}
