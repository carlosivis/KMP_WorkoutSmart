package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutSummaryModel
import dev.carlosivis.workoutsmart.shared.GroupResponse
import dev.carlosivis.workoutsmart.shared.UserResponse

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