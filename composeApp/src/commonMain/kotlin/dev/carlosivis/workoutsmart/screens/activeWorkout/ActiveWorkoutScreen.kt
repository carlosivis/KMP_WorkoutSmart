package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TopAppBar
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
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_save
import dev.carlosivis.workoutsmart.composeResources.elapsed_time_label
import dev.carlosivis.workoutsmart.composeResources.exercise_default
import dev.carlosivis.workoutsmart.composeResources.exit_active_workout_confirmation_message
import dev.carlosivis.workoutsmart.composeResources.exit_without_saving_title
import dev.carlosivis.workoutsmart.composeResources.finished_workout_tittle
import dev.carlosivis.workoutsmart.composeResources.mark_as_completed_button
import dev.carlosivis.workoutsmart.composeResources.rest_time_label
import dev.carlosivis.workoutsmart.composeResources.skip_button
import dev.carlosivis.workoutsmart.composeResources.start_rest_button
import dev.carlosivis.workoutsmart.composeResources.start_workout_button
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import dev.carlosivis.workoutsmart.screens.components.TimeWheelPicker
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workout.name) },
                navigationIcon = {
                    IconButton(onClick = { action(ActiveWorkoutViewAction.AttemptToNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                }
            )
        },
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
                if (!state.isWorkoutActive) {
                    Button(
                        onClick = { action(ActiveWorkoutViewAction.StartWorkout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Medium)
                    ) {
                        Text(stringResource(Res.string.start_workout_button))
                    }
                } else {
                    Text(
                        text = stringResource(
                            Res.string.elapsed_time_label,
                            state.elapsedTime
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Medium),
                        fontSize = FontSizes.BodyLarge
                    )
                }

                val lazyListState = rememberLazyListState()
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.workout.exercises) { exercise ->
                        AnimatedVisibility(
                            visible = !state.completedExercises.contains(exercise.name),
                            modifier = Modifier.fillParentMaxWidth(0.9f)
                        ) {
                            ExerciseCard(
                                exercise = exercise,
                                restTimer = { action(ActiveWorkoutViewAction.StartTimer) },
                                onMarkAsCompleted = {
                                    action(
                                        ActiveWorkoutViewAction.MarkExerciseAsCompleted(
                                            exercise.name
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if (state.isRestTimerActive) {
                RestTimerCard(
                    time = state.restTimerValue,
                    onStop = { action(ActiveWorkoutViewAction.StopTimer) }
                )
            }

            if (state.showRestTimerSelector) {
                RestTimerSelector(
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

        if (state.showFinishedWorkoutDialog) {
            CustomDialog(
                title = stringResource(Res.string.finished_workout_tittle),
                message = "",
                onConfirm = { action(ActiveWorkoutViewAction.SaveWorkoutHistory) },
                onCancel = { action(ActiveWorkoutViewAction.DismissFinishedWorkoutDialog) }
            )
        }
    }
}

@Composable
private fun RestTimerSelector(
    currentTime: Int,
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMinutes = currentTime / 60
    val initialSeconds = currentTime % 60

    var selectedMinutes by remember { mutableStateOf(initialMinutes) }
    var selectedSeconds by remember { mutableStateOf(initialSeconds) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(Dimens.Medium)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Medium)
            ) {
                Text(
                    text = stringResource(Res.string.rest_time_label),
                    fontSize = FontSizes.TitleMedium,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeWheelPicker(
                        range = 0..59,
                        initialValue = selectedMinutes,
                        onValueChange = { selectedMinutes = it }
                    )
                    Text(":", fontSize = FontSizes.TitleLarge)
                    TimeWheelPicker(
                        range = 0..59,
                        initialValue = selectedSeconds,
                        onValueChange = { selectedSeconds = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            text = stringResource(Res.string.action_cancel),
                        )
                    }
                    TextButton(
                        onClick = {
                            val totalSeconds = selectedMinutes * 60 + selectedSeconds
                            onTimeSelected(totalSeconds)
                        },
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseModel,
    restTimer: () -> Unit,
    onMarkAsCompleted: () -> Unit
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
                .size(250.dp)
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
            contentDescription = "Toggle Menu"
        )
    }
    AnimatedVisibility(isMenuExpanded) {
        Row(
            modifier = Modifier.padding(Dimens.Medium).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSelectFinish ,
            ) {
                Text("Finalizar")
                Icon(Icons.Filled.Check, contentDescription = "Finish workout")
            }
            TextButton(
                onClick = onSelectRestTimer ,
            ) {
                Text("Timer")
                Icon(Icons.Filled.Timelapse, contentDescription = "Change rest timer")
            }
        }
    }
}
