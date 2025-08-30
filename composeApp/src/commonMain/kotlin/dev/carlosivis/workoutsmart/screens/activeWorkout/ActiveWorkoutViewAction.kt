package dev.carlosivis.workoutsmart.screens.activeWorkout

sealed class ActiveWorkoutViewAction {
    object StartWorkout : ActiveWorkoutViewAction()
    object PauseWorkout : ActiveWorkoutViewAction()
    object ResumeWorkout : ActiveWorkoutViewAction()
    object StopWorkout : ActiveWorkoutViewAction()
    object Tick : ActiveWorkoutViewAction()
    object NavigateBack : ActiveWorkoutViewAction()
}