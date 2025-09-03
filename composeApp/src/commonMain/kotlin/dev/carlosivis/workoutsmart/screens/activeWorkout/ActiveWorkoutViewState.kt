package dev.carlosivis.workoutsmart.screens.activeWorkout

import dev.carlosivis.workoutsmart.models.WorkoutModel

data class ActiveWorkoutViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExitConfirmationDialog: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val elapsedTime: Long = 0L,
    val workout: WorkoutModel = WorkoutModel.empty()
)
