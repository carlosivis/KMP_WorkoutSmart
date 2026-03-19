package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import dev.carlosivis.workoutsmart.models.GroupResponse
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
                    navigator = object : HomeNavigator {
                        override fun toCreateWorkout() {
                            navigation.push(Configuration.CreateWorkout)
                        }

                        override fun toActiveWorkout(id: Long) {
                            navigation.push(Configuration.ActiveWorkout(id))
                        }

                        override fun toEditWorkout(id: Long) {
                            navigation.push(Configuration.EditWorkout(id))
                        }

                        override fun toProfile() {
                            navigation.push(Configuration.Profile)
                        }

                        override fun toGroups(groups: List<GroupResponse>?) {
                            navigation.push(Configuration.Groups(groups))
                        }

                        override fun toRanking(group: GroupResponse) {
                            navigation.push(Configuration.Ranking(group))
                        }
                    }
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
                    workoutIdToEdit = config.id
                )
            )

            is Configuration.ActiveWorkout -> Child.ActiveWorkout(
                ActiveWorkoutComponent(
                    componentContext = context,
                    workoutId = config.id,
                    onNavigateBack = { navigation.pop() }
                )
            )

            is Configuration.Settings -> Child.Settings(
                SettingsComponent(
                    componentContext = context,
                    onNavigateBack = { navigation.pop() }
                )
            )

            is Configuration.Profile -> Child.Profile(
                ProfileComponent(
                    componentContext = context,
                    navigator = object : ProfileNavigator {
                        override fun back() {
                            navigation.pop()
                        }

                        override fun toSettings() {
                            navigation.push(Configuration.Settings)
                        }
                    }
                )
            )

            is Configuration.Groups -> Child.Groups(
                GroupsComponent(
                    componentContext = context,
                    groups = config.groups,
                    navigator = object : GroupsNavigator {
                        override fun toRanking(group: GroupResponse) {
                            navigation.push(Configuration.Ranking(group))
                        }

                        override fun back() {
                            navigation.pop()
                        }
                    }
                )
            )

            is Configuration.Ranking -> Child.Ranking(
                RankingComponent(
                    componentContext = context,
                    group = config.group,
                    navigator = object : RankingNavigator {
                        override fun back() {
                            navigation.pop()
                        }
                    }
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
        data class Profile(val component: ProfileComponent) : Child()
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
    data class EditWorkout(val id: Long) : Configuration()

    @Serializable
    data class ActiveWorkout(val id: Long) : Configuration()

    @Serializable
    data object Settings : Configuration()

    @Serializable
    data object Profile : Configuration()

    @Serializable
    data class Groups(val groups: List<GroupResponse>?) : Configuration()

    @Serializable
    data class Ranking(val group: GroupResponse) : Configuration()
}
