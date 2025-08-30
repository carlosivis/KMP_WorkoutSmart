package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.lifecycle.ViewModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActiveWorkoutViewModel(
    val workout: WorkoutModel,
    private val onNavigateBack: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(ActiveWorkoutViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: ActiveWorkoutViewAction) {
        when (action) {
            ActiveWorkoutViewAction.NavigateBack -> onNavigateBack()
            ActiveWorkoutViewAction.PauseWorkout -> TODO()
            ActiveWorkoutViewAction.ResumeWorkout -> TODO()
            ActiveWorkoutViewAction.StartWorkout -> TODO()
            ActiveWorkoutViewAction.StopWorkout -> TODO()
            ActiveWorkoutViewAction.Tick -> TODO()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

}
