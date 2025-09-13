package dev.carlosivis.workoutsmart.screens.activeWorkout

sealed class ActiveWorkoutViewAction {
    object StartWorkout : ActiveWorkoutViewAction()
    object PauseWorkout : ActiveWorkoutViewAction()
    object StopWorkout : ActiveWorkoutViewAction()
    object Tick : ActiveWorkoutViewAction()
    object StartTimer : ActiveWorkoutViewAction()
    object StopTimer : ActiveWorkoutViewAction()
    object CancelNavigateBack : ActiveWorkoutViewAction()
    object AttemptToNavigateBack : ActiveWorkoutViewAction()
    object NavigateBack : ActiveWorkoutViewAction()
    object GetWorkout : ActiveWorkoutViewAction()
    object SaveWorkoutHistory : ActiveWorkoutViewAction()
    data class UpdateRestTime(val seconds: Int) : ActiveWorkoutViewAction()

}