package dev.carlosivis.workoutsmart.screens.createWorkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.Shapes
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import dev.carlosivis.workoutsmart.composeResources.add_exercise_button
import dev.carlosivis.workoutsmart.composeResources.create_workout_screen_title
import dev.carlosivis.workoutsmart.composeResources.exercise_name_label
import dev.carlosivis.workoutsmart.composeResources.exercise_notes_label
import dev.carlosivis.workoutsmart.composeResources.exercise_repetitions_label
import dev.carlosivis.workoutsmart.composeResources.exercise_series_label
import dev.carlosivis.workoutsmart.composeResources.exit_unsaved_changes_message
import dev.carlosivis.workoutsmart.composeResources.exit_without_saving_title
import dev.carlosivis.workoutsmart.composeResources.save_workout_button
import dev.carlosivis.workoutsmart.composeResources.workout_description_label
import dev.carlosivis.workoutsmart.composeResources.workout_title_label
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    viewModel: CreateWorkoutViewModel
) {
    val state  by viewModel.state.collectAsState()
    val action: (CreateWorkoutViewAction) -> Unit = viewModel::dispatchAction

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.create_workout_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { action(CreateWorkoutViewAction.AttemptToNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Content(
            modifier = Modifier.padding(paddingValues),
            state = state,
            action = action
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: CreateWorkoutViewState,
    action: (CreateWorkoutViewAction) -> Unit){

    if (state.showExitConfirmationDialog) {
        CustomDialog(
            title = stringResource(Res.string.exit_without_saving_title),
            message = stringResource(Res.string.exit_unsaved_changes_message),
            onConfirm = { action(CreateWorkoutViewAction.NavigateBack) },
            onCancel = { action(CreateWorkoutViewAction.CancelNavigateBack) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.Medium),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.workout.name,
            onValueChange = { name ->
                action(CreateWorkoutViewAction.AddName(name))
            },
            label = { Text(stringResource(Res.string.workout_title_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.Medium))

        TextField(
            value = state.workout.description,
            onValueChange = { description ->
                action(CreateWorkoutViewAction.AddDescription(description))
            },
            label = { Text(stringResource(Res.string.workout_description_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.Medium))

        if (state.isAddingExercise) {
            NewExerciseCard(
                exercise = state.newExercise,
                onExerciseChange = { action(CreateWorkoutViewAction.UpdateNewExercise(it)) },
                onConfirm = { action(CreateWorkoutViewAction.ConfirmNewExercise) },
                onCancel = { action(CreateWorkoutViewAction.CancelAddingExercise) }
            )
        } else {
            Button(onClick = { action(CreateWorkoutViewAction.StartAddingExercise) }) {
                Text(stringResource(Res.string.add_exercise_button))
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Medium))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.Medium)
        ) {
            itemsIndexed(state.workout.exercises) { index, exercise ->
                ExerciseInput(
                    exercise = exercise,
                    onExerciseChange = { updatedExercise ->
                        action(CreateWorkoutViewAction.UpdateExercise(index, updatedExercise))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Medium))

        Button(
            onClick = { action(CreateWorkoutViewAction.SaveWorkout) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.save_workout_button))
        }
    }
}

@Composable
private fun NewExerciseCard(
    exercise: ExerciseModel,
    onExerciseChange: (ExerciseModel) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Medium)
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            ExerciseInput(exercise = exercise, onExerciseChange = onExerciseChange)
            Spacer(modifier = Modifier.height(Dimens.Medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onCancel) {
                    Text(stringResource(Res.string.action_cancel))
                }
                Spacer(modifier = Modifier.width(Dimens.Small))
                Button(onClick = onConfirm) {
                    Text(stringResource(Res.string.action_confirm))
                }
            }
        }
    }
}

@Composable
private fun ExerciseInput(
    exercise: ExerciseModel,
    onExerciseChange: (ExerciseModel) -> Unit
) {
    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = exercise.name,
                onValueChange = { onExerciseChange(exercise.copy(name = it)) },
                label = { Text(stringResource(Res.string.exercise_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            )

            Spacer(modifier = Modifier.height(Dimens.Small))

            TextField(
                value = exercise.notes,
                onValueChange = { onExerciseChange(exercise.copy(notes = it)) },
                label = { Text(stringResource(Res.string.exercise_notes_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            )

            Spacer(modifier = Modifier.height(Dimens.Small))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = if (exercise.series == 0) "" else exercise.series.toString(),
                    onValueChange = {
                        val series = it.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(series = series))
                    },
                    label = { Text(stringResource(Res.string.exercise_series_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Shapes.ExtraLarge)
                )

                Spacer(modifier = Modifier.width(Dimens.Small))

                TextField(
                    value = if (exercise.repetitions == 0) "" else exercise.repetitions.toString(),
                    onValueChange = {
                        val repetitions = it.toIntOrNull() ?: 0
                        onExerciseChange(exercise.copy(repetitions = repetitions))
                    },
                    label = { Text(stringResource(Res.string.exercise_repetitions_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Shapes.ExtraLarge)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            //TODO("change to image picker")

        }
    }
}
