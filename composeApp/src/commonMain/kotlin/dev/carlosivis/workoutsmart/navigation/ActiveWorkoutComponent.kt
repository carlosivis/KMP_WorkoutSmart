package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ActiveWorkoutComponent(
    componentContext: ComponentContext,
    val workout: WorkoutModel,
    val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = ActiveWorkoutViewModel(
        workout = workout,
        repository = get(),
        onNavigateBack = onNavigateBack
    )
}
