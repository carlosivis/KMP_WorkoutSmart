package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.lifecycle.ViewModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActiveWorkoutViewModel(
    val workout: WorkoutModel,
    private val onNavigateBack: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(ActiveWorkoutViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: ActiveWorkoutViewAction) {
        when (action) {
            ActiveWorkoutViewAction.NavigateBack -> onNavigateBack()
            ActiveWorkoutViewAction.PauseWorkout -> TODO()
            ActiveWorkoutViewAction.StartWorkout -> TODO()
            ActiveWorkoutViewAction.StopWorkout -> TODO()
            ActiveWorkoutViewAction.Tick -> TODO()
            ActiveWorkoutViewAction.AttemptToNavigateBack -> attemptToNavigateBack()
            ActiveWorkoutViewAction.CancelNavigateBack -> cancelNavigateBack()
            ActiveWorkoutViewAction.StartTimer -> TODO()
            ActiveWorkoutViewAction.GetWorkout -> getWorkout()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun getWorkout() {
        _state.update { it.copy(workout = workout) }
    }
    private fun attemptToNavigateBack() {
        _state.update { it.copy(showExitConfirmationDialog = true) }
    }

    private fun cancelNavigateBack() {
        _state.update { it.copy(showExitConfirmationDialog = false) }
    }
}
