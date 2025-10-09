package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ActiveWorkoutViewModel(
    val workout: WorkoutModel,
    private val repository: WorkoutRepository,
    private val onNavigateBack: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(ActiveWorkoutViewState())
    val state = _state.asStateFlow()
    private var timerJob: Job? = null
    private var workoutTimerJob: Job? = null

    fun dispatchAction(action: ActiveWorkoutViewAction) {
        when (action) {
            is ActiveWorkoutViewAction.NavigateBack -> onNavigateBack()
            is ActiveWorkoutViewAction.StartWorkout -> startWorkout()
            is ActiveWorkoutViewAction.StopWorkout -> stopWorkout()
            is ActiveWorkoutViewAction.Tick -> timerTick()
            is ActiveWorkoutViewAction.AttemptToNavigateBack -> attemptToNavigateBack()
            is ActiveWorkoutViewAction.CancelNavigateBack -> cancelNavigateBack()
            is ActiveWorkoutViewAction.StartTimer -> startTimer(action.exerciseName)
            is ActiveWorkoutViewAction.StopTimer -> stopTimer()
            is ActiveWorkoutViewAction.GetWorkout -> getWorkout()
            is ActiveWorkoutViewAction.UpdateRestTime -> updateRestTime(action.seconds)
            is ActiveWorkoutViewAction.SaveWorkoutHistory -> saveWorkoutHistory()
            is ActiveWorkoutViewAction.MarkExerciseAsCompleted -> markExerciseAsCompleted(action.exerciseName)
            is ActiveWorkoutViewAction.DismissFinishedWorkoutDialog -> dismissFinishedWorkoutDialog()
            is ActiveWorkoutViewAction.ToggleRestTimer -> toggleRestTimer()
            is ActiveWorkoutViewAction.ExitWithoutSave -> exitWithoutSave()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun getWorkout() {
        _state.update {
            it.copy(
                workout = workout,
                remainingSeries = workout.exercises.associate { exercise -> exercise.name to exercise.series }
            )
        }
    }

    private fun stopWorkout() {
        _state.update { it.copy(showExitUnfinishedDialog = true) }
    }
    private fun attemptToNavigateBack() {
        _state.update { it.copy(showExitConfirmationDialog = true) }
    }

    private fun cancelNavigateBack() {
        _state.update { it.copy(showExitConfirmationDialog = false) }
    }

    private fun updateRestTime(seconds: Int) {
        _state.update { it.copy(restTime = seconds, showRestTimerSelector = false) }
    }

    private fun startTimer(exerciseName: String) {
        val currentSeries = _state.value.remainingSeries[exerciseName] ?: 0
        if (currentSeries > 0) {
            val newSeries = currentSeries - 1
            _state.update {
                it.copy(
                    remainingSeries = it.remainingSeries.toMutableMap().apply {
                        this[exerciseName] = newSeries
                    }
                )
            }
            if (newSeries == 0) {
                markExerciseAsCompleted(exerciseName)
            }
        }

        timerJob?.cancel()
        _state.update { it.copy(isRestTimerActive = true, restTimerValue = it.restTime) }
        if (!_state.value.isWorkoutActive) startWorkout()
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

    private fun startWorkout() {
        workoutTimerJob?.cancel()
        _state.update { it.copy(isWorkoutActive = true, elapsedTime = 0L) }
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }

    private fun saveWorkoutHistory() {
        viewModelScope.launch {
            setLoading(true)
            val timestamp: Long = Clock.System.now().epochSeconds
            repository.insertHistory(workout.name, timestamp)
            _state.update { it.copy(showFinishedWorkoutDialog = false, showExitUnfinishedDialog = false) }
            setLoading(false)
            onNavigateBack()
        }
    }

    private fun exitWithoutSave(){
        _state.update { it.copy(showExitUnfinishedDialog = false) }
        onNavigateBack()
    }

    private fun markExerciseAsCompleted(exerciseName: String) {
        _state.update { currentState ->
            val updatedCompletedExercises = currentState.completedExercises + exerciseName
            val allExercisesCompleted = updatedCompletedExercises.size == currentState.workout.exercises.size && currentState.workout.exercises.isNotEmpty()
            currentState.copy(
                completedExercises = updatedCompletedExercises,
                showFinishedWorkoutDialog = allExercisesCompleted
            )
        }
    }

    private fun dismissFinishedWorkoutDialog() {
        _state.update { it.copy(showFinishedWorkoutDialog = false) }
    }
    
    private fun toggleRestTimer() {
        _state.update { it.copy(showRestTimerSelector = !it.showRestTimerSelector) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        workoutTimerJob?.cancel()
    }
}