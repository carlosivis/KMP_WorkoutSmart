package dev.carlosivis.workoutsmart

import HomeScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dev.carlosivis.workoutsmart.navigation.RootComponent
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutScreen
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutScreen

@Composable
fun App(root: RootComponent) {
    MaterialTheme {
        Children(
            stack = root.childStack,
            animation = stackAnimation(slide())
        ) {
            when (val instance = it.instance) {
                is RootComponent.Child.Home -> HomeScreen(instance.component.viewModel)
                is RootComponent.Child.CreateWorkout -> CreateWorkoutScreen(instance.component.viewModel)
                is RootComponent.Child.ActiveWorkout -> ActiveWorkoutScreen(instance.component.viewModel)
            }
        }
    }
}
