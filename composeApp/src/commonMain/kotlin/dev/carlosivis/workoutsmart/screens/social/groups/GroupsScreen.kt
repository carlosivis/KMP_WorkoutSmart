package dev.carlosivis.workoutsmart.screens.social.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.groups_screen_create_group_button
import dev.carlosivis.workoutsmart.composeResources.groups_screen_join_group_button
import dev.carlosivis.workoutsmart.composeResources.groups_screen_title
import dev.carlosivis.workoutsmart.composeResources.groups_screen_user_rank
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.CustomCreateGroupDialog
import dev.carlosivis.workoutsmart.screens.components.CustomJoinGroupDialog
import dev.carlosivis.workoutsmart.screens.components.RankingEmptyState
import dev.carlosivis.workoutsmart.screens.components.loadings.PlaceholderHighlight
import dev.carlosivis.workoutsmart.screens.components.loadings.placeholder
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.utils.errorSnackbar
import org.jetbrains.compose.resources.stringResource

@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (GroupsViewAction) -> Unit = viewModel::dispatchAction

    Content(state, action)

}

@Composable
fun Content(
    state: GroupsViewState,
    action: (GroupsViewAction) -> Unit,
) {

    val errorHandler = errorSnackbar(
        error = state.error,
        action = { action(GroupsViewAction.CleanError) }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = errorHandler) }
    ) { paddingValues ->

        AnimatedVisibility(
            visible = state.showAddGroup,
        ) {
            CustomCreateGroupDialog(
                onDismiss = { action(GroupsViewAction.ShowAddGroup) },
                onConfirm = { action(GroupsViewAction.CreateGroup(it)) },
                onCancel = { action(GroupsViewAction.ShowAddGroup) }
            )
        }
        AnimatedVisibility(
            visible = state.showAddInvite,
        ) {
            CustomJoinGroupDialog(
                onDismiss = { action(GroupsViewAction.ShowAddInvite) },
                onConfirm = { action(GroupsViewAction.JoinGroup(it)) },
                onCancel = { action(GroupsViewAction.ShowAddInvite) }
            )
        }
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(Dimens.Medium)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { action(GroupsViewAction.Navigate.Back) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_back)
                    )
                }
                Text(
                    text = stringResource(Res.string.groups_screen_title),
                    textAlign = TextAlign.Center, fontSize = FontSizes.TitleMedium
                )
            }
            if (state.groups.isNullOrEmpty()) {
                RankingEmptyState()
            } else {
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = state.groups,
                    ) { group ->
                        GroupCard(
                            modifier = Modifier.padding(Dimens.Small)
                                .placeholder(state.isLoading, PlaceholderHighlight.shimmer()),
                            group = group,
                            onClick = { action(GroupsViewAction.Navigate.Ranking(group)) })
                    }
                }
            }

            CustomActionButton(
                modifier = Modifier.padding(Dimens.Medium),
                title = stringResource(Res.string.groups_screen_create_group_button),
                icon = Icons.Default.Add,
                onClick = { action(GroupsViewAction.ShowAddGroup) }
            )
            Spacer(Modifier.height(Dimens.Medium))

            CustomActionButton(
                modifier = Modifier.padding(Dimens.Medium),
                title = stringResource(Res.string.groups_screen_join_group_button),
                icon = Icons.AutoMirrored.Filled.Login,
                onClick = { action(GroupsViewAction.ShowAddInvite) }
            )
        }
    }
}

@Composable
fun GroupCard(group: GroupResponse, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        val initials = remember(group.name) {
            group.name.trim()
                .split("\\s+".toRegex())
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
                .ifEmpty { "?" }
        }
        Column(
            modifier = Modifier.padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(Dimens.ImageSizeMedium),
                shape = CircleShape,
                shadowElevation = Dimens.Medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = Dimens.ExtraSmall,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (initials.length > 1) FontSizes.HeadlineMedium else FontSizes.HeadlineLarge
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Small))
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(Dimens.Small))
            Text(
                text = stringResource(Res.string.groups_screen_user_rank, group.userPosition),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
fun CustomActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Medium)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(Dimens.ImageSizeSmall)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.Large)
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}


@Preview
@Composable
fun GroupCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        GroupCard(
            group = GroupResponse(
                id = 1,
                name = "Teste Nome",
                inviteCode = "123456",
                userScore = 100,
                userPosition = 1
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
fun CustomActionButtonPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        CustomActionButton(
            modifier = Modifier.padding(Dimens.Medium),
            title = "Teste",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = GroupsViewState(
                groups = listOf(
                    GroupResponse(1, "Group 1", "abc", 100, 1),
                    GroupResponse(1, "Group 6", "abc", 100, 6),
                    GroupResponse(1, "Group 21", "abc", 100, 2),
                    GroupResponse(1, "Group 1", "abc", 100, 3),
                    GroupResponse(2, "Group 2", "def", 200, 2),
                )
            ),
            action = {}
        )
    }
}