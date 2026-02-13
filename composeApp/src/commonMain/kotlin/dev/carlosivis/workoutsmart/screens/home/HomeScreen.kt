package dev.carlosivis.workoutsmart.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.utils.errorSnackbar
import dev.carlosivis.workoutsmart.utils.formatDateToString
import dev.carlosivis.workoutsmart.utils.formatDuration
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (HomeViewAction) -> Unit = viewModel::dispatchAction
    Content(
        state = state,
        action = viewModel::dispatchAction
    )
    LaunchedEffect(Unit) {
        action(HomeViewAction.GetWorkouts)
        action(HomeViewAction.GetHistory)
        action(HomeViewAction.GetUserProfile)
        action(HomeViewAction.GetGroups)
    }
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

    val errorHandler = errorSnackbar(
        error = state.error,
        action = { action(HomeViewAction.CleanError) }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = errorHandler) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { action(HomeViewAction.Navigate.CreateWorkout) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(Dimens.Medium),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            ) {
                Icon(Icons.Filled.Add, stringResource(Res.string.create_workout_fab))
            }
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
                        modifier = Modifier.placeholder(
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

