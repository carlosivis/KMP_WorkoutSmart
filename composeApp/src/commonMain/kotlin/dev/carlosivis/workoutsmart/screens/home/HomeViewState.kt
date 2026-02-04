package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.models.WorkoutModel

data class HomeViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val workouts: List<WorkoutModel> = emptyList(),
    val history: List<HistoryModel> = emptyList(),
    val workoutToDelete: WorkoutModel? = null,
    val user: UserResponse? = null
)