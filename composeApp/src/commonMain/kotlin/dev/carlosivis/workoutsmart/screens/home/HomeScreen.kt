package dev.carlosivis.workoutsmart.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.create_workout_fab
import dev.carlosivis.workoutsmart.composeResources.delete_action
import dev.carlosivis.workoutsmart.composeResources.delete_workout_message
import dev.carlosivis.workoutsmart.composeResources.delete_workout_title
import dev.carlosivis.workoutsmart.composeResources.edit_action
import dev.carlosivis.workoutsmart.composeResources.home_screen_duration
import dev.carlosivis.workoutsmart.composeResources.home_screen_login
import dev.carlosivis.workoutsmart.composeResources.home_screen_my_profile
import dev.carlosivis.workoutsmart.composeResources.home_screen_title
import dev.carlosivis.workoutsmart.composeResources.ranking_carousel_title
import dev.carlosivis.workoutsmart.composeResources.saved_workouts_section_title
import dev.carlosivis.workoutsmart.composeResources.workout_history_section_title
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import dev.carlosivis.workoutsmart.screens.components.CustomTopBar
import dev.carlosivis.workoutsmart.screens.components.RankingCarousel
import dev.carlosivis.workoutsmart.screens.components.RankingLoginRequiredCard
import dev.carlosivis.workoutsmart.screens.components.loadings.PlaceholderHighlight
import dev.carlosivis.workoutsmart.screens.components.loadings.placeholder
import dev.carlosivis.workoutsmart.utils.AppSnackbarHost
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.utils.formatDateToString
import dev.carlosivis.workoutsmart.utils.formatDuration
import dev.carlosivis.workoutsmart.utils.rememberSnackbarHandler
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (HomeViewAction) -> Unit = viewModel::dispatchAction
    Content(
        state = state,
        action = action
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: HomeViewState,
    action: (HomeViewAction) -> Unit
) {
    if (state.workoutToDelete != null) {
        CustomDialog(
            title = stringResource(Res.string.delete_workout_title),
            message = stringResource(Res.string.delete_workout_message, state.workoutToDelete.name),
            onConfirm = { action(HomeViewAction.ConfirmDeleteWorkout) },
            onCancel = { action(HomeViewAction.CancelDeleteWorkout) }
        )
    }

    val (snackbarHostState, snackbarType) = rememberSnackbarHandler(
        error = state.error,
        message = state.message,
        action = { action(HomeViewAction.CleanMessages) }
    )

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = snackbarHostState, type = snackbarType) },
        floatingActionButton = {
            ExpandableFABMenuAction(
                onSelectAdd = {action(HomeViewAction.Navigate.CreateWorkout)},
                onSelectRegister = {action(HomeViewAction.ShowRegisterWorkoutDialog)}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.Medium)
        ) {

            CustomTopBar(
                iconNavBack = null,
                title = stringResource(Res.string.home_screen_title),
                rightIcon = if (state.user != null) Icons.Default.Person else Icons.AutoMirrored.Filled.Login,
                rightIconDescription = if (state.user != null) Res.string.home_screen_my_profile else Res.string.home_screen_login,
                onRightIconClick = { action(HomeViewAction.Navigate.Profile) }
            )

            Spacer(Modifier.height(Dimens.Medium))

            Text(
                text = stringResource(Res.string.ranking_carousel_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Medium))

            AnimatedContent(
                targetState = state.user,
                transitionSpec = {
                    if (initialState == null || targetState == null) {
                        (fadeIn(animationSpec = tween(600)) +
                                slideInHorizontally { width -> width / 2 })
                            .togetherWith(
                                fadeOut(animationSpec = tween(600)) +
                                        slideOutHorizontally { width -> -width / 2 })
                    } else {
                        fadeIn(animationSpec = tween(1200)) togetherWith fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    }
                },
            ) { user ->
                if (user == null) {
                    RankingLoginRequiredCard(
                        modifier = Modifier.placeholder(
                            state.isLoading,
                            PlaceholderHighlight.shimmer()
                        ),
                        onLoginClick = { action(HomeViewAction.Navigate.Profile) })
                } else {
                    RankingCarousel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .placeholder(
                            state.isLoading,
                            PlaceholderHighlight.shimmer()
                        ),
                        groups = state.groups,
                        onCardClick = { action(HomeViewAction.Navigate.Groups) },
                        onGroupClick = { action(HomeViewAction.Navigate.Ranking(it)) }
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Small))

            Text(
                stringResource(Res.string.saved_workouts_section_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Small))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.workouts) { workout ->
                    WorkoutCard(
                        modifier = Modifier
                            .padding(vertical = Dimens.Small)
                            .placeholder(state.isLoading, PlaceholderHighlight.shimmer()),
                        workout = workout,
                        navigate = { action(HomeViewAction.Navigate.Workout(workout)) },
                        delete = { action(HomeViewAction.AttemptDeleteWorkout(workout)) },
                        edit = { action(HomeViewAction.Navigate.Edit(workout)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Large))
            Text(
                stringResource(Res.string.workout_history_section_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Small))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.history) { history ->
                    HistoryCard(
                        modifier = Modifier
                            .padding(vertical = Dimens.Small)
                            .placeholder(state.isLoading, PlaceholderHighlight.shimmer()),
                        history = history
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    modifier: Modifier = Modifier,
    workout: WorkoutModel, navigate: () -> Unit = {},
    delete: () -> Unit = {}, edit: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { navigate() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = Dimens.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                workout.name,
                modifier = Modifier.weight(1f),
                fontSize = FontSizes.BodyLarge
            )

            Row {
                IconButton(onClick = edit) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(Res.string.edit_action)
                    )
                }
                IconButton(onClick = delete) {
                    Icon(
                        Icons.Filled.Delete,
                        stringResource(Res.string.delete_action)
                    )
                }
            }
        }

    }

}

@Composable
private fun HistoryCard(
    modifier: Modifier = Modifier,
    history: HistoryModel
) {
    val formattedDate = formatDateToString(history.date)
    val formattedDuration = formatDuration(history.duration)
    Card(
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "${history.workoutName}\n$formattedDate ${
                stringResource(
                    Res.string.home_screen_duration,
                    formattedDuration
                )
            }",
            modifier = Modifier.padding(Dimens.Medium),
            fontSize = FontSizes.BodyLarge
        )
    }
}
@Composable
fun ExpandableFABMenuAction(
    onSelectAdd: () -> Unit = {},
    onSelectRegister: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.Medium),
        contentAlignment = Alignment.BottomEnd
    ) {

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.Small),
                modifier = Modifier.padding(bottom = Dimens.ImageSizeMedium)

            ) {
                FabMiniAction(
                    label = stringResource(Res.string.create_workout_fab),
                    icon = Icons.Filled.Add,
                    onClick = {
                        !expanded
                        onSelectAdd()
                    }
                )

                FabMiniAction(
                    label = "Treino Avulso",
                    icon = Icons.Filled.Edit,
                    onClick = {
                        !expanded
                        onSelectRegister()
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(Shapes.ExtraLarge)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun FabMiniAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimens.Small,
        tonalElevation = Dimens.Small,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Small),
            modifier = Modifier.padding(
                horizontal = Dimens.Large,
                vertical = Dimens.Small
            )
        ) {
            Text(
                text = label,
                fontSize = FontSizes.TitleSmall
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = HomeViewState(
                workouts = listOf(
                    WorkoutModel(
                        id = 1,
                        name = "Workout A",
                        description = "Description A",
                        exercises = emptyList()
                    ),
                    WorkoutModel(
                        id = 2,
                        name = "Workout B",
                        description = "Description B",
                        exercises = emptyList()
                    )
                ),
                history = listOf(
                    HistoryModel(
                        id = 1,
                        date = 1678886400000,
                        workoutName = "Workout A",
                        duration = 3600
                    ),
                    HistoryModel(
                        id = 2,
                        date = 1678972800000,
                        workoutName = "Workout B",
                        duration = 3600
                    )
                )
            ),
            action = {}
        )

    }
}

