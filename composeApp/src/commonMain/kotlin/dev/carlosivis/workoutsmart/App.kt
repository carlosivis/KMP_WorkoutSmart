package dev.carlosivis.workoutsmart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dev.carlosivis.workoutsmart.Utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.navigation.RootComponent
import dev.carlosivis.workoutsmart.repository.SettingsRepository
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutScreen
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutScreen
import dev.carlosivis.workoutsmart.screens.home.HomeScreen
import dev.carlosivis.workoutsmart.screens.profile.ProfileScreen
import dev.carlosivis.workoutsmart.screens.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun App(root: RootComponent) {

    val settingsRepository = koinInject<SettingsRepository>()
    val settings by settingsRepository.getSettings()
        .collectAsState(initial = SettingsModel.default())

    WorkoutsSmartTheme(themeMode = settings.themeMode) {
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
                is RootComponent.Child.Login -> ProfileScreen(instance.component.viewModel)
            }
        }
    }
}
