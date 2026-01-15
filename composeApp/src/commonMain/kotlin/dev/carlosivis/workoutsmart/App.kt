package dev.carlosivis.workoutsmart

import HomeScreen
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dev.carlosivis.workoutsmart.Utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.navigation.RootComponent
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutScreen
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutScreen
import dev.carlosivis.workoutsmart.screens.settings.SettingsScreen

@Composable
fun App(root: RootComponent) {
    WorkoutsSmartTheme {
        Children(
            stack = root.childStack,
            animation = stackAnimation(slide())
        ) {
            when (val instance = it.instance) {
                is RootComponent.Child.Home -> HomeScreen(instance.component.viewModel)
                is RootComponent.Child.CreateWorkout -> CreateWorkoutScreen(instance.component.viewModel)
                is RootComponent.Child.ActiveWorkout -> ActiveWorkoutScreen(instance.component.viewModel)
                is RootComponent.Child.EditWorkout -> CreateWorkoutScreen(instance.component.viewModel)
                is RootComponent.Child.Settings -> SettingsScreen(instance.component.viewModel)
            }
        }
    }
}
