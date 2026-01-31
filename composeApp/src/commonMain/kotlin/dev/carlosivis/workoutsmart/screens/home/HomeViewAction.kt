package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.WorkoutModel

sealed class HomeViewAction {
    object GetWorkouts : HomeViewAction()
    object GetHistory : HomeViewAction()
    data class AttemptDeleteWorkout(val workout: WorkoutModel) : HomeViewAction()
    object ConfirmDeleteWorkout : HomeViewAction()
    object CancelDeleteWorkout : HomeViewAction()

    object Navigate {
        data class Details(val workoutId: Int) : HomeViewAction()
        object CreateWorkout : HomeViewAction()
        data class Workout(val workout: WorkoutModel) : HomeViewAction()
        data class Edit(val workout: WorkoutModel) : HomeViewAction()
        object Settings : HomeViewAction()
        object Login : HomeViewAction()
    }
}