package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.ic_user_placeholder
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_copy
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_copy_code_description
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_empty_ranking
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_invite_friends
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_invite_friends_title
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_invite_message
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_share
import dev.carlosivis.workoutsmart.composeResources.ranking_screen_share_code
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.RankingMember
import dev.carlosivis.workoutsmart.plataform.copyText
import dev.carlosivis.workoutsmart.plataform.shareText
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.CustomTopBar
import dev.carlosivis.workoutsmart.screens.components.loadings.PlaceholderHighlight
import dev.carlosivis.workoutsmart.screens.components.loadings.placeholder
import dev.carlosivis.workoutsmart.utils.AppSnackbarHost
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
import dev.carlosivis.workoutsmart.utils.rememberSnackbarHandler
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val inviteMessage = stringResource(Res.string.ranking_screen_invite_message, state.group?.name ?: "", state.group?.inviteCode ?: "")

    AnimatedVisibility(
        visible = state.showInviteCode,
        modifier = Modifier.fillMaxSize(),
    ) {
        InviteCodeBottomSheet(
            inviteCode = state.group!!.inviteCode,
            onCopy = {
                scope.launch {
                    clipboard.copyText(state.group.inviteCode)
                }
                action(RankingViewAction.CopyInviteCode)
            },
            onShare = {
                scope.launch {
                    shareText(inviteMessage)
                }
            },
            onDismiss = { action(RankingViewAction.ShowInviteCode) }
        )
    }

    val (snackbarHostState, snackbarType) = rememberSnackbarHandler(
        error = state.error,
        message = state.message,
        action = { action(RankingViewAction.CleanMessages) }
    )
    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = snackbarHostState, type = snackbarType) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { action(RankingViewAction.ShowInviteCode) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(Dimens.Medium),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = Dimens.Small),
                    contentAlignment = Alignment.Center,
                ) {
                    Row {
                        Icon(Icons.Filled.PersonAdd, contentDescription = null)
                        Spacer(Modifier.width(Dimens.Small))
                        Text(stringResource(Res.string.ranking_screen_invite_friends))
                    }
                }
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(Dimens.Medium)
                .fillMaxSize()
        ) {
            CustomTopBar(
                onNavBackClick = { action(RankingViewAction.Navigate.Back) },
                title = state.group?.name
            )

            RankingPodiumCard(
                state.podium,
                modifier = Modifier.placeholder(
                    state.isLoading, PlaceholderHighlight.shimmer()
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = Dimens.Medium)
                    .placeholder(
                        state.isLoading, PlaceholderHighlight.shimmer()
                    ),
                verticalArrangement = Arrangement.spacedBy(Dimens.Small)
            ) {
                items(
                    items = state.others
                ) { member ->
                    RankingRowCard(
                        member
                    )
                }
                item {
                    if (state.others.isEmpty() && !state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(horizontal = Dimens.Medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.ranking_screen_empty_ranking),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingPodiumCard(
    top3: List<RankingMember>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(Dimens.Medium)
            .fillMaxHeight(if (top3.size < 3) 0.35f else 0.5f)
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
                if (top3.any { it.position == pos }) {
                    top3.firstOrNull { it.position == pos }?.let { member ->
                        PodiumItem(
                            rankingMember = member,
                            podium = PodiumStyle.fromPosition(member.position),
                            modifier = Modifier.weight(1f)

                        )
                    }
                } else {
                    Spacer(Modifier.weight(1f))
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
            contentAlignment = Alignment.Center,
        ) {

        }
    }
}

@Composable
fun RankingRowCard(
    member: RankingMember,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(Shapes.Medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.Medium,
                    vertical = Dimens.Small
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Medium)
        ) {

            Text(
                text = "#${member.position}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(Dimens.ExtraLarge),
                textAlign = TextAlign.Center
            )

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                AsyncImage(
                    model = member.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    placeholder = painterResource(Res.drawable.ic_user_placeholder),
                    error = painterResource(Res.drawable.ic_user_placeholder),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${member.score} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteCodeBottomSheet(
    inviteCode: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(Res.string.ranking_screen_invite_friends_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(Dimens.Small))

            Text(
                text = stringResource(Res.string.ranking_screen_share_code),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Dimens.Medium))

            InviteCodeCard(
                inviteCode = inviteCode,
                onCopy = onCopy
            )

            Spacer(Modifier.height(Dimens.Large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Medium)
            ) {
                ActionButton(
                    label = stringResource(Res.string.ranking_screen_copy),
                    onClick = onCopy
                )

                ActionButton(
                    label = stringResource(Res.string.ranking_screen_share),
                    onClick = onShare
                )
            }

            Spacer(Modifier.height(Dimens.Large))
        }
    }
}

@Composable
fun InviteCodeCard(
    inviteCode: String,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(Shapes.Large),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimens.Large,
                    vertical = Dimens.Medium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = inviteCode,
                style = MaterialTheme.typography.headlineSmall,
                letterSpacing = 2.sp
            )

            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = stringResource(Res.string.ranking_screen_copy_code_description)
            )
        }
    }
}


@Composable
private fun ActionButton(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
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

@Preview
@Composable
fun RankingRowCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        RankingRowCard(
            member = RankingMember(
                position = 4,
                displayName = "User 4",
                photoUrl = "",
                score = 400
            )
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = RankingViewState(
                group = GroupResponse(
                    id = 1,
                    name = "Preview Group",
                    inviteCode = "PREVIEW",
                    userScore = 1000,
                    userPosition = 1
                ),
                podium = listOf(
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
                ),
                others = listOf(
                    RankingMember(
                        position = 4,
                        displayName = "User 12",
                        photoUrl = "",
                        score = 600
                    ), RankingMember(
                        position = 5,
                        displayName = "User 34",
                        photoUrl = "",
                        score = 600
                    ),
                    RankingMember(
                        position = 6,
                        displayName = "User 4",
                        photoUrl = "",
                        score = 400
                    )
                )
            ),
            action = {}
        )
    }
}

@Preview
@Composable
private fun ContentPreviewWithFewMembers() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = RankingViewState(
                group = GroupResponse(
                    id = 1,
                    name = "Grupo Pequeno",
                    inviteCode = "SMALL",
                    userScore = 1000,
                    userPosition = 1
                ),
                podium = listOf(
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
                    )
                )
            ),
            action = {}
        )
    }
}

@Preview
@Composable
private fun ContentPreviewOneUser() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = RankingViewState(
                group = GroupResponse(
                    id = 1,
                    name = "Grupo Solo",
                    inviteCode = "SOLO",
                    userScore = 1000,
                    userPosition = 1
                ),
                podium = listOf(
                    RankingMember(
                        position = 1,
                        displayName = "User 1",
                        photoUrl = "",
                        score = 1000
                    )
                )
            ),
            action = {}
        )
    }
}

