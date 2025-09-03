package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CustomDialog

@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
) {
    val state  by viewModel.state.collectAsState()
    val action: (ActiveWorkoutViewAction) -> Unit = viewModel::dispatchAction

    LaunchedEffect(Unit) {
        action(ActiveWorkoutViewAction.GetWorkout)
    }
    Content(
        state = state,
        action = action
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: ActiveWorkoutViewState,
    action: (ActiveWorkoutViewAction) -> Unit){

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workout.name) },
                navigationIcon = {
                    IconButton(onClick = { action(ActiveWorkoutViewAction.AttemptToNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ){ paddingValues ->

        if(state.showExitConfirmationDialog){
            CustomDialog(
                title = "Sair sem salvar?",
                message = "Você ainda não terminou o treino. Deseja realmente sair?",
                onConfirm = { action(ActiveWorkoutViewAction.NavigateBack) },
                onCancel = { action(ActiveWorkoutViewAction.CancelNavigateBack) }
            )
        }
        rememberScrollState()
        LazyRow (modifier = Modifier.padding(paddingValues, )) {
            items(state.workout.exercises) {
                ExerciseCard(exercise = it, restTimer = { action(ActiveWorkoutViewAction.StartTimer) })
            }
        }
    }

}

@Composable
private fun ExerciseCard(
    exercise: ExerciseModel,
    restTimer: () -> Unit){

    Card(modifier = Modifier.fillMaxHeight(0.7F)) {
        Text(exercise.name)
        //TODO("Change database to use a blob image")
        //Image(painter = exercise.imageUrl, contentDescription = null))

        Text(exercise.notes,
            fontSize = FontSizes.BodySmall,
        )
        Button(onClick = restTimer) {
            Text("Descanso")
        }
    }
}