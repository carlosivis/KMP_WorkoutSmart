package dev.carlosivis.workoutsmart.screens.activeWorkout

import dev.carlosivis.workoutsmart.models.WorkoutModel

data class ActiveWorkoutViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExitConfirmationDialog: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val elapsedTime: Long = 0L,
    val restTime: Int = 60,
    val isRestTimerActive: Boolean = false,
    val restTimerValue: Int = 0,
    val workout: WorkoutModel = WorkoutModel.empty(),
    val completedExercises: Set<String> = emptySet(),
    val showFinishedWorkoutDialog: Boolean = false,
    val showRestTimerSelector: Boolean = false
)
