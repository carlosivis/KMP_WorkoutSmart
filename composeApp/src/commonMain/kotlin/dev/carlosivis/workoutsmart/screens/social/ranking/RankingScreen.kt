package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.ic_user_placeholder
import dev.carlosivis.workoutsmart.models.RankingMember
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.CustomTopBar
import dev.carlosivis.workoutsmart.utils.BronzeColor
import dev.carlosivis.workoutsmart.utils.BronzeGradient
import dev.carlosivis.workoutsmart.utils.BronzeGradientLinear
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.GoldColor
import dev.carlosivis.workoutsmart.utils.GoldGradient
import dev.carlosivis.workoutsmart.utils.GoldGradientLinear
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.SilverColor
import dev.carlosivis.workoutsmart.utils.SilverGradient
import dev.carlosivis.workoutsmart.utils.SilverGradientLinear
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.utils.errorSnackbar
import org.jetbrains.compose.resources.painterResource

@Composable
fun RankingScreen(
    viewModel: RankingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (RankingViewAction) -> Unit = viewModel::dispatchAction

    Content(state, action)
}

@Composable
private fun Content(
    state: RankingViewState,
    action: (RankingViewAction) -> Unit,
) {
    val errorHandler = errorSnackbar(
        error = state.error,
        action = { action(RankingViewAction.CleanError) }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = errorHandler) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(Dimens.Medium)
        ) {
            CustomTopBar(
                onNavBackClick = { action(RankingViewAction.Navigate.Back) },
                title = state.group?.name
            )
            RankingPodiumCard(state.ranking)
        }
    }
}

@Composable
fun RankingPodiumCard(top3: List<RankingMember>) {
    Card(
        modifier = Modifier.padding(Dimens.Medium)
            .fillMaxHeight(0.5f)
            .fillMaxWidth()

    ) {
        val podiumOrder = listOf(2, 1, 3)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Medium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Large),
            verticalAlignment = Alignment.Bottom
        ) {
            podiumOrder.forEach { pos ->
                top3.firstOrNull { it.position == pos }?.let { member ->
                    PodiumItem(
                        rankingMember = member,
                        podium = PodiumStyle.fromPosition(member.position),
                        modifier = Modifier.weight(1f)

                    )
                }
            }
        }

    }

}

@Composable
fun PodiumItem(
    rankingMember: RankingMember,
    podium: PodiumStyle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight(podium.heightFraction),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Surface(
                modifier = Modifier
                    .size(
                        if (podium == PodiumStyle.TOP1) Dimens.ImageSizeSmall
                        else Dimens.ExtraLarge
                    ),
                shape = CircleShape,
                border = BorderStroke(Dimens.ExtraSmall, podium.baseColor),
            ) {
                AsyncImage(
                    model = rankingMember.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    placeholder = painterResource(Res.drawable.ic_user_placeholder),
                    error = painterResource(Res.drawable.ic_user_placeholder),
                    contentScale = ContentScale.Crop
                )
            }
            Surface(
                modifier = Modifier.offset(y = 6.dp).scale(0.55f),
                shape = CircleShape,
                color = podium.baseColor,
                border = BorderStroke(Dimens.ExtraSmall, podium.linearGradient),
                tonalElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = Dimens.Small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${rankingMember.position}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }


        Spacer(Modifier.height(Dimens.ExtraSmall))

        Text(
            text = rankingMember.displayName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(Dimens.ExtraSmall))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = Shapes.Large, topEnd = Shapes.Large))
                .background(podium.verticalGradient),
            contentAlignment = Alignment.Center
        ) {}
    }
}


enum class PodiumStyle(
    val heightFraction: Float,
    val baseColor: Color,
    val verticalGradient: Brush,
    val linearGradient: Brush
) {
    TOP1(0.85f, GoldColor, GoldGradient, GoldGradientLinear),
    TOP2(0.65f, SilverColor, SilverGradient, SilverGradientLinear),
    TOP3(0.5f, BronzeColor, BronzeGradient, BronzeGradientLinear);

    companion object {
        fun fromPosition(position: Int): PodiumStyle =
            when (position) {
                1 -> TOP1
                2 -> TOP2
                3 -> TOP3
                else -> TOP3
            }
    }
}

@Preview
@Composable
fun RankingPodiumCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        RankingPodiumCard(
            top3 = listOf(
                RankingMember(
                    position = 1,
                    displayName = "User 1",
                    photoUrl = "",
                    score = 1000
                ),
                RankingMember(
                    position = 2,
                    displayName = "User 2",
                    photoUrl = "",
                    score = 800
                ),
                RankingMember(
                    position = 3,
                    displayName = "User 3",
                    photoUrl = "",
                    score = 600
                )
            )
        )
    }
}
