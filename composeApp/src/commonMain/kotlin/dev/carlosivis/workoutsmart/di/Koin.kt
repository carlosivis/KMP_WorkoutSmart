package dev.carlosivis.workoutsmart.di

import dev.carlosivis.workoutsmart.database.DatabaseHelper
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.repository.WorkoutRepositoryImpl
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun platformModule(): org.koin.core.module.Module

fun initKoin() = startKoin {
    modules(commonModule, platformModule())
}

val commonModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { (workout: WorkoutModel, onNavigateBack: () -> Unit) ->
        ActiveWorkoutViewModel(workout, get(), onNavigateBack)
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

    single<Clock> {
        Clock.System
    }
}
