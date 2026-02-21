package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.models.GroupResponse

sealed class HomeViewAction {
    object GetUserProfile : HomeViewAction()
    object GetGroups : HomeViewAction()
    data class AttemptDeleteWorkout(val id: Long, val name: String) : HomeViewAction()
    object ConfirmDeleteWorkout : HomeViewAction()
    object CancelDeleteWorkout : HomeViewAction()
    object CleanMessages : HomeViewAction()
    object ShowRegisterWorkoutDialog : HomeViewAction()
    data class RegisterWorkoutLog(val log: WorkoutLogRequest) : HomeViewAction()
    object Refresh : HomeViewAction()

    object Navigate {
        object CreateWorkout : HomeViewAction()
        data class Workout(val id: Long) : HomeViewAction()
        data class Edit(val id: Long) : HomeViewAction()
        object Profile : HomeViewAction()
        object Groups : HomeViewAction()
        data class Ranking(val group: GroupResponse) : HomeViewAction()
    }
}