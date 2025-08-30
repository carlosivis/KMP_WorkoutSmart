package dev.carlosivis.workoutsmart.screens.home

import dev.carlosivis.workoutsmart.models.WorkoutModel

sealed class HomeViewAction {
    object GetWorkouts : HomeViewAction()
    object GetHistory : HomeViewAction()

    object Navigate {
        data class Details(val workoutId: Int) : HomeViewAction()
        object CreateWorkout : HomeViewAction()
        data class Workout(val workout: WorkoutModel) : HomeViewAction()
    }
}