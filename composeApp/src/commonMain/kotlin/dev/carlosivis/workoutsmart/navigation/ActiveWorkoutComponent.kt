package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewAction
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class ActiveWorkoutComponent(
    componentContext: ComponentContext,
    val workoutId: Long,
    val onNavigateBack: () -> Unit
) : ComponentContext by componentContext, KoinComponent {
    val viewModel: ActiveWorkoutViewModel = get { parametersOf(workoutId, onNavigateBack) }

    private val backCallback = BackCallback {
        viewModel.dispatchAction(ActiveWorkoutViewAction.AttemptToNavigateBack)
    }

    init {
        backHandler.register(backCallback)
    }
}
