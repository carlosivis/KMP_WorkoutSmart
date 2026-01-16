package dev.carlosivis.workoutsmart.screens.activeWorkout

sealed class ActiveWorkoutViewAction {
    object StartWorkout : ActiveWorkoutViewAction()
    object StopWorkout : ActiveWorkoutViewAction()
    object Tick : ActiveWorkoutViewAction()
    data class StartTimer(val exerciseName: String) : ActiveWorkoutViewAction()
    object StopTimer : ActiveWorkoutViewAction()
    object CancelNavigateBack : ActiveWorkoutViewAction()
    object AttemptToNavigateBack : ActiveWorkoutViewAction()
    object NavigateBack : ActiveWorkoutViewAction()
    object GetWorkout : ActiveWorkoutViewAction()
    object SaveWorkoutHistory : ActiveWorkoutViewAction()
    object ExitWithoutSave : ActiveWorkoutViewAction()
    object DismissFinishedWorkoutDialog : ActiveWorkoutViewAction()
    object ToggleRestTimer : ActiveWorkoutViewAction()
    data class UpdateRestTime(val seconds: Int) : ActiveWorkoutViewAction()
    data class MarkExerciseAsCompleted(val exerciseName: String) : ActiveWorkoutViewAction()
    object GetSettings : ActiveWorkoutViewAction()

}