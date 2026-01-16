package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.Utils.WhitePure
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.action_not_saved
import dev.carlosivis.workoutsmart.composeResources.action_save
import dev.carlosivis.workoutsmart.composeResources.active_workout_change_rest_timer
import dev.carlosivis.workoutsmart.composeResources.active_workout_finish
import dev.carlosivis.workoutsmart.composeResources.active_workout_finish_workout
import dev.carlosivis.workoutsmart.composeResources.active_workout_reps
import dev.carlosivis.workoutsmart.composeResources.active_workout_sets
import dev.carlosivis.workoutsmart.composeResources.active_workout_timer
import dev.carlosivis.workoutsmart.composeResources.active_workout_toggle_menu
import dev.carlosivis.workoutsmart.composeResources.elapsed_time_label
import dev.carlosivis.workoutsmart.composeResources.exercise_default
import dev.carlosivis.workoutsmart.composeResources.exit_active_workout_confirmation_message
import dev.carlosivis.workoutsmart.composeResources.exit_confirmation_save_or_cancel
import dev.carlosivis.workoutsmart.composeResources.exit_without_saving_title
import dev.carlosivis.workoutsmart.composeResources.finished_workout_message
import dev.carlosivis.workoutsmart.composeResources.finished_workout_tittle
import dev.carlosivis.workoutsmart.composeResources.mark_as_completed_button
import dev.carlosivis.workoutsmart.composeResources.skip_button
import dev.carlosivis.workoutsmart.composeResources.start_rest_button
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import dev.carlosivis.workoutsmart.screens.components.KeepScreenOn
import dev.carlosivis.workoutsmart.screens.components.RestTimerSelectorDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
) {
    val state by viewModel.state.collectAsState()
    val action: (ActiveWorkoutViewAction) -> Unit = viewModel::dispatchAction

    LaunchedEffect(Unit) {
        action(ActiveWorkoutViewAction.GetWorkout)
        action(ActiveWorkoutViewAction.GetSettings)
    }
    Content(
        state = state,
        action = action
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Content(
    state: ActiveWorkoutViewState,
    action: (ActiveWorkoutViewAction) -> Unit
) {

    KeepScreenOn(enabled = state.settings.keepScreenOn)
    Scaffold(
        floatingActionButton = {
            ExpandableFABMenu(
                onSelectFinish = { action(ActiveWorkoutViewAction.StopWorkout) },
                onSelectRestTimer = { action(ActiveWorkoutViewAction.ToggleRestTimer) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { action(ActiveWorkoutViewAction.AttemptToNavigateBack) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                    Text(
                        text = state.workout.name,
                        fontSize = FontSizes.TitleMedium,
                        textAlign = TextAlign.Center
                    )
                }


                Text(
                    text = stringResource(
                        Res.string.elapsed_time_label,
                        state.elapsedTime
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium),
                    fontSize = FontSizes.BodyLarge,
                )


                val lazyListState = rememberLazyListState()
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
                    contentPadding = PaddingValues(horizontal = Dimens.Medium),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Small)
                ) {
                    items(state.workout.exercises) { exercise ->
                        AnimatedVisibility(
                            visible = !state.completedExercises.contains(exercise.name),
                            modifier = Modifier.fillParentMaxWidth(0.9f)
                        ) {
                            ExerciseCard(
                                exercise = exercise,
                                restTimer = { action(ActiveWorkoutViewAction.StartTimer(exercise.name)) },
                                onMarkAsCompleted = {
                                    action(
                                        ActiveWorkoutViewAction.MarkExerciseAsCompleted(
                                            exercise.name
                                        )
                                    )
                                },
                                remainingSeries = state.remainingSeries[exercise.name] ?: 0
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                state.isRestTimerActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                RestTimerCard(
                    time = state.restTimerValue,
                    onStop = { action(ActiveWorkoutViewAction.StopTimer) }
                )
            }


            AnimatedVisibility(
                state.showRestTimerSelector,
                enter = scaleIn(),
                exit = shrinkOut()
            ) {
                RestTimerSelectorDialog(
                    currentTime = state.restTime,
                    onTimeSelected = {
                        action(ActiveWorkoutViewAction.UpdateRestTime(it))
                    },
                    onDismiss = { action(ActiveWorkoutViewAction.ToggleRestTimer) }
                )
            }
        }

        if (state.showExitConfirmationDialog) {
            CustomDialog(
                title = stringResource(Res.string.exit_without_saving_title),
                message = stringResource(Res.string.exit_active_workout_confirmation_message),
                onConfirm = { action(ActiveWorkoutViewAction.NavigateBack) },
                onCancel = { action(ActiveWorkoutViewAction.CancelNavigateBack) }
            )
        }

        if (state.showExitUnfinishedDialog) {
            CustomDialog(
                title = stringResource(Res.string.exit_without_saving_title),
                message = stringResource(Res.string.exit_confirmation_save_or_cancel),
                onConfirm = { action(ActiveWorkoutViewAction.SaveWorkoutHistory) },
                onCancel = { action(ActiveWorkoutViewAction.ExitWithoutSave) },
                confirmButtonText = stringResource(Res.string.action_save),
                cancelButtonText = stringResource(Res.string.action_not_saved)
            )
        }
        if (state.showFinishedWorkoutDialog) {
            CustomDialog(
                title = stringResource(Res.string.finished_workout_tittle),
                message = stringResource(Res.string.finished_workout_message),
                onConfirm = { action(ActiveWorkoutViewAction.SaveWorkoutHistory) },
                onCancel = { action(ActiveWorkoutViewAction.DismissFinishedWorkoutDialog) }
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseModel,
    restTimer: () -> Unit,
    onMarkAsCompleted: () -> Unit,
    remainingSeries: Int
) {
    Card(
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .padding(vertical = Dimens.Small)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.Small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.name,
                        fontSize = FontSizes.TitleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Dimens.Small),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    TextButton(
                        onClick = onMarkAsCompleted,
                        modifier = Modifier.padding(start = Dimens.Small)
                    ) {
                        Text(
                            stringResource(Res.string.mark_as_completed_button),
                            fontSize = FontSizes.BodySmall,
                            maxLines = 1
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    ExerciseImage(exercise = exercise)
                }

                Row(Modifier.fillMaxWidth().padding(bottom = Dimens.Small)) {
                    Text(
                        text = exercise.notes,
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.Center
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {

                    Text(
                        text = stringResource(Res.string.active_workout_sets, remainingSeries),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(
                            Res.string.active_workout_reps,
                            exercise.repetitions
                        ), textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = restTimer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.Small)
            ) {
                Text(stringResource(Res.string.start_rest_button))
            }
        }
    }
}

@Composable
private fun RestTimerCard(time: Int, onStop: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.25f)
                .fillMaxWidth(0.50f)
                .clip(RoundedCornerShape(percent = 100))
                .background(Color.DarkGray)
                .border(BorderStroke(2.dp, WhitePure), RoundedCornerShape(percent = 100)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = time.toString(),
                    fontSize = FontSizes.HeadlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = Dimens.Medium)
                )
                Button(onClick = onStop) {
                    Text(stringResource(Res.string.skip_button))
                }
            }
        }
    }
}

@Composable
private fun ExerciseImage(exercise: ExerciseModel) {
    if (exercise.image != null) {
        AsyncImage(
            model = exercise.image,
            contentDescription = exercise.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(Res.drawable.exercise_default),
            contentDescription = exercise.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ExpandableFABMenu(
    onSelectFinish: () -> Unit = {},
    onSelectRestTimer: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { isMenuExpanded = !isMenuExpanded },
    ) {
        Icon(
            imageVector = if (isMenuExpanded) Icons.Filled.Close else Icons.Filled.Engineering,
            contentDescription = stringResource(Res.string.active_workout_toggle_menu)
        )
    }
    AnimatedVisibility(isMenuExpanded) {
        Row(
            modifier = Modifier.padding(Dimens.Medium).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSelectFinish,
            ) {
                Text(stringResource(Res.string.active_workout_finish))
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(Res.string.active_workout_finish_workout)
                )
            }
            TextButton(
                onClick = onSelectRestTimer,
            ) {
                Text(stringResource(Res.string.active_workout_timer))
                Icon(
                    Icons.Filled.Timelapse,
                    contentDescription = stringResource(Res.string.active_workout_change_rest_timer)
                )
            }
        }
    }
}
