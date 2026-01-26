package dev.carlosivis.workoutsmart.di

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.database.DatabaseHelper
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.repository.SettingsRepository
import dev.carlosivis.workoutsmart.repository.SettingsRepositoryImpl
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.repository.WorkoutRepositoryImpl
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

expect fun platformModule(): org.koin.core.module.Module

fun initKoin() = startKoin {
    modules(commonModule, platformModule())
}

@OptIn(ExperimentalTime::class)
val commonModule = module {
    viewModel { (navigator: HomeNavigator) ->
        HomeViewModel(get(), navigator) }
    viewModel { (workout: WorkoutModel, onNavigateBack: () -> Unit) ->
        ActiveWorkoutViewModel(workout, get(), get(), onNavigateBack, get())
    }
    viewModel { (onNavigateBack: () -> Unit) ->
        CreateWorkoutViewModel(get(), onNavigateBack)
    }

    single {
        DatabaseHelper(get(), Dispatchers.Default)
    }

    single<WorkoutRepository> {
        WorkoutRepositoryImpl(get())
    }

    single<kotlin.time.Clock> {
        kotlin.time.Clock.System
    }

    single<Settings> { Settings() }

    single<SettingsRepository>{
        SettingsRepositoryImpl(get())
    }
}
