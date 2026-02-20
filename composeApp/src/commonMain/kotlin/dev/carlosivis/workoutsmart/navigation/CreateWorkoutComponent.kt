package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class CreateWorkoutComponent(
    componentContext: ComponentContext,
    val onNavigateBack: () -> Unit,
    val workoutIdToEdit: Long? = null
) : ComponentContext by componentContext, KoinComponent {
    val viewModel: CreateWorkoutViewModel =
        get { parametersOf(onNavigateBack, workoutIdToEdit) }
}
