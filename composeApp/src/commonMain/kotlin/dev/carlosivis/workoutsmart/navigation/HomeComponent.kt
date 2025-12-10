package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeComponent(
    componentContext: ComponentContext,
    val onNavigateToCreateWorkout: () -> Unit,
    val onNavigateToWorkout: (WorkoutModel) -> Unit,
    val onNavigateToEditWorkout: (WorkoutModel) -> Unit = {}
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = HomeViewModel(
        repository = get(),
        onNavigateToCreateWorkout = onNavigateToCreateWorkout,
        onNavigateToWorkout = onNavigateToWorkout,
        onNavigateToEditWorkout = onNavigateToEditWorkout
    )
}
