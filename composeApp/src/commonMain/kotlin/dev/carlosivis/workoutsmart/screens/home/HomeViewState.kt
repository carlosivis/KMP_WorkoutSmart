package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel

data class HomeViewState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showRegisterWorkoutDialog: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val workouts: List<WorkoutSummaryModel> = emptyList(),
    val history: List<HistoryModel> = emptyList(),
    val workoutIdToDelete: Long? = null,
    val workoutToDelete: String? = null,
    val user: UserResponse? = null,
    val groups: List<GroupResponse> = emptyList()
)