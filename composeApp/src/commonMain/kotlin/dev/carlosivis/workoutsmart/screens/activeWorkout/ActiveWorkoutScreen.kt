package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
) {
    //val state by viewModel.state.collectAsState()
    val action: (ActiveWorkoutViewAction) -> Unit = viewModel::dispatchAction
    Content(
        state = viewModel.state.collectAsState().value,
        action = action
    )

}
private fun Content(
    state: ActiveWorkoutViewState,
    action: (ActiveWorkoutViewAction) -> Unit){

}