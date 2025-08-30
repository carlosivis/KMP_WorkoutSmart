package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import dev.carlosivis.workoutsmart.models.WorkoutModel
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = null,
        initialStack = { listOf(Configuration.Home) },
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(
        config: Configuration,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Configuration.Home -> Child.Home(
                HomeComponent(
                    componentContext = context,
                    onNavigateToCreateWorkout = { navigation.push(Configuration.CreateWorkout) },
                    onNavigateToWorkout = { workout -> navigation.push(Configuration.ActiveWorkout(workout)) }
                )
            )
            is Configuration.CreateWorkout -> Child.CreateWorkout(
                CreateWorkoutComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() }
                )
            )
            is Configuration.ActiveWorkout -> Child.ActiveWorkout(
                ActiveWorkoutComponent(
                    componentContext = context,
                    workout = config.workout,
                    onNavigateBack = { navigation.pop() }
                )
            )
        }
    }

    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class CreateWorkout(val component: CreateWorkoutComponent) : Child()
        data class ActiveWorkout(val component: ActiveWorkoutComponent) : Child()
    }
}

@Serializable
sealed class Configuration {
    @Serializable
    data object Home : Configuration()

    @Serializable
    data object CreateWorkout : Configuration()

    @Serializable
    data class ActiveWorkout(val workout: WorkoutModel) : Configuration()
}
