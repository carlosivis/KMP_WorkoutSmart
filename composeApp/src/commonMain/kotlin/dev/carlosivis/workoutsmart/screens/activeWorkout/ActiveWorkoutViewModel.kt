package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActiveWorkoutViewModel(
    val workout: WorkoutModel,
    private val onNavigateBack: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(ActiveWorkoutViewState())
    val state = _state.asStateFlow()
    private var timerJob: Job? = null
    private var workoutTimerJob: Job? = null

    fun dispatchAction(action: ActiveWorkoutViewAction) {
        when (action) {
            is ActiveWorkoutViewAction.NavigateBack -> onNavigateBack()
            is ActiveWorkoutViewAction.PauseWorkout -> TODO()
            is ActiveWorkoutViewAction.StartWorkout -> startWorkout()
            is ActiveWorkoutViewAction.StopWorkout -> TODO()
            is ActiveWorkoutViewAction.Tick -> timerTick()
            is ActiveWorkoutViewAction.AttemptToNavigateBack -> attemptToNavigateBack()
            is ActiveWorkoutViewAction.CancelNavigateBack -> cancelNavigateBack()
            is ActiveWorkoutViewAction.StartTimer -> startTimer()
            is ActiveWorkoutViewAction.StopTimer -> stopTimer()
            is ActiveWorkoutViewAction.GetWorkout -> getWorkout()
            is ActiveWorkoutViewAction.UpdateRestTime -> updateRestTime(action.seconds)
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

    private fun updateRestTime(seconds: Int) {
        _state.update { it.copy(restTime = seconds) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isRestTimerActive = true, restTimerValue = it.restTime) }
        timerJob = viewModelScope.launch {
            while (state.value.restTimerValue > 0) {
                delay(1000)
                dispatchAction(ActiveWorkoutViewAction.Tick)
            }
            dispatchAction(ActiveWorkoutViewAction.StopTimer)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isRestTimerActive = false) }
    }

    private fun timerTick() {
        _state.update { it.copy(restTimerValue = it.restTimerValue - 1) }
    }

    private fun startWorkout(){
        workoutTimerJob?.cancel()
        _state.update { it.copy(isWorkoutActive = true, elapsedTime = 0L) }
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        workoutTimerJob?.cancel()
    }
}
