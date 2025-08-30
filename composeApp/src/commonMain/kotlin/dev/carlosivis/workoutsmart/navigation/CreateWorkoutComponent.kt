package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CreateWorkoutComponent(
    componentContext: ComponentContext,
    val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = CreateWorkoutViewModel(
        repository = get(),
        onNavigateBack = onNavigateBack
    )
}
