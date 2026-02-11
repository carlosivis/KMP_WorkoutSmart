package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.ranking_carousel_empty_subtitle
import dev.carlosivis.workoutsmart.composeResources.ranking_carousel_empty_title
import dev.carlosivis.workoutsmart.composeResources.ranking_carousel_title
import dev.carlosivis.workoutsmart.composeResources.ranking_login_required_subtitle
import dev.carlosivis.workoutsmart.composeResources.ranking_login_required_title
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.utils.BronzeGradient
import dev.carlosivis.workoutsmart.utils.DefaultRankColor
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.GoldColor
import dev.carlosivis.workoutsmart.utils.GoldGradient
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.SilverGradient
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun RankingCarousel(
    groups: List<GroupResponse>,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(enabled = true, onClick = { onCardClick() })
    ) {
        if (groups.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.ranking_carousel_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Medium))
            Card(
                shape = RoundedCornerShape(Shapes.ExtraLarge),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            ) {

                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.Medium),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Medium)
                ) {
                    items(groups) { group ->
                        RankingBadgeCard(group)
                    }
                }
            }
        } else {
            RankingEmptyState()
        }
    }

}

@Composable
private fun RankingBadgeCard(group: GroupResponse) {
    val (backgroundBrush, iconVec) = when (group.userPosition) {
        1 -> GoldGradient to Icons.Filled.EmojiEvents
        2 -> SilverGradient to Icons.Filled.MilitaryTech
        3 -> BronzeGradient to Icons.Filled.MilitaryTech
        else -> {
            Brush.verticalGradient(
                listOf(DefaultRankColor.copy(alpha = 0.7f), DefaultRankColor)
            ) to Icons.Filled.Star
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Medium),
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = FontSizes.BodySmall),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = iconVec,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Dimens.ExtraLarge)
                    )

                    Text(
                        text = "#${group.userPosition}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = FontSizes.HeadlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun RankingEmptyState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedTrophyIcon()

            Spacer(Modifier.height(Dimens.Medium))

            Text(
                text = stringResource(Res.string.ranking_carousel_empty_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Dimens.Small))

            Text(
                text = stringResource(Res.string.ranking_carousel_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

        }
    }
}


@Composable
fun AnimatedTrophyIcon() {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 0.90f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = Icons.Default.EmojiEvents,
        contentDescription = null,
        tint = GoldColor,
        modifier = Modifier
            .size(Dimens.ImageSizeMedium)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
fun RankingLoginRequiredCard(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onLoginClick() },
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(Dimens.ImageSizeSmall)
            )

            Spacer(Modifier.height(Dimens.Small))

            Text(
                text = stringResource(Res.string.ranking_login_required_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(Dimens.Small))

            Text(
                text = stringResource(Res.string.ranking_login_required_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun RankingLoginRequiredCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        RankingLoginRequiredCard(onLoginClick = {})
    }
}

@Preview
@Composable
fun RankingCarouselEmptyPreview(){
    WorkoutsSmartTheme(ThemeMode.DARK){
        RankingCarousel(groups = emptyList())
    }
}


@Preview
@Composable
fun RankingCarouselPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        RankingCarousel(
            groups = listOf(
                GroupResponse(1, "Group 1", "abc", 100, 1),
                GroupResponse(1, "Group 6", "abc", 100, 6),
                GroupResponse(1, "Group 21", "abc", 100, 2),
                GroupResponse(1, "Group 1", "abc", 100, 3),
                GroupResponse(2, "Group 2", "def", 200, 2)
            )
        )
    }
}
