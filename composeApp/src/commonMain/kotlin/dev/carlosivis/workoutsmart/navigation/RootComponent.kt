package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
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
                    navigator = HomeNavigator(
                        toCreateWorkout = { navigation.push(Configuration.CreateWorkout) },
                        toActiveWorkout = { workout ->
                            navigation.push(
                                Configuration.ActiveWorkout(workout)
                            )
                        },
                        toEditWorkout = { workout ->
                            navigation.push(
                                Configuration.EditWorkout(workout)
                            )
                        },
                        toProfile = { navigation.push(Configuration.Profile) }
                    )
                )
            )

            is Configuration.CreateWorkout -> Child.CreateWorkout(
                CreateWorkoutComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() }
                )
            )

            is Configuration.EditWorkout -> Child.EditWorkout(
                CreateWorkoutComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() },
                    workoutToEdit = config.workout
                )
            )

            is Configuration.ActiveWorkout -> Child.ActiveWorkout(
                ActiveWorkoutComponent(
                    componentContext = context,
                    workout = config.workout,
                    onNavigateBack = { navigation.pop() }
                )
            )

            is Configuration.Settings -> Child.Settings(
                SettingsComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() }
                )
            )

            is Configuration.Profile -> Child.Login(
                ProfileComponent(
                    componentContext = context,
                    navigator = ProfileNavigator(
                        back = { navigation.pop() },
                        toSettings = { navigation.push(Configuration.Settings) }
                    )
                )
            )

            is Configuration.Groups -> Child.Groups(
                GroupsComponent(
                    componentContext = context,
                    navigator = GroupsNavigator(
                        toRanking = { id -> navigation.push(Configuration.Ranking(id)) },
                        back = { navigation.pop() }
                    )
                )
            )

            is Configuration.Ranking -> Child.Ranking(
                RankingComponent(
                    componentContext = context,
                    navigator = RankingNavigator(
                        back = { navigation.pop() }
                    )
                )
            )
        }
    }


    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class CreateWorkout(val component: CreateWorkoutComponent) : Child()
        data class EditWorkout(val component: CreateWorkoutComponent) : Child()
        data class ActiveWorkout(val component: ActiveWorkoutComponent) : Child()
        data class Settings(val component: SettingsComponent) : Child()
        data class Login(val component: ProfileComponent) : Child()
        data class Groups(val component: GroupsComponent) : Child()
        data class Ranking(val component: RankingComponent) : Child()
    }
}

@Serializable
sealed class Configuration {
    @Serializable
    data object Home : Configuration()

    @Serializable
    data object CreateWorkout : Configuration()

    @Serializable
    data class EditWorkout(val workout: WorkoutModel) : Configuration()

    @Serializable
    data class ActiveWorkout(val workout: WorkoutModel) : Configuration()

    @Serializable
    data object Settings : Configuration()

    @Serializable
    data object Profile : Configuration()

    @Serializable
    data object Groups : Configuration()

    @Serializable
    data class Ranking(val id: Int) : Configuration()
}
