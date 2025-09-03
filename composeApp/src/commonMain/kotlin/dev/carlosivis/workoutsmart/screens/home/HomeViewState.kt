package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel

data class HomeViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val workouts: List<WorkoutModel> = emptyList(),
    val history: List<HistoryModel> = emptyList(),
    val workoutToDelete: WorkoutModel? = null
)