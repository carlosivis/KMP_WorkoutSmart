package dev.carlosivis.workoutsmart.di

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.BuildConfig
import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSourceImpl
import dev.carlosivis.workoutsmart.data.local.datasource.UserLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.UserLocalDataSourceImpl
import dev.carlosivis.workoutsmart.data.remote.datasource.AuthRemoteDataSource
import dev.carlosivis.workoutsmart.data.remote.datasource.AuthRemoteDataSourceImpl
import dev.carlosivis.workoutsmart.data.remote.service.AuthService
import dev.carlosivis.workoutsmart.database.DatabaseHelper
import dev.carlosivis.workoutsmart.domain.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.repository.AuthRepository
import dev.carlosivis.workoutsmart.repository.AuthRepositoryImpl
import dev.carlosivis.workoutsmart.repository.SettingsRepository
import dev.carlosivis.workoutsmart.repository.SettingsRepositoryImpl
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.repository.WorkoutRepositoryImpl
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import dev.carlosivis.workoutsmart.screens.profile.ProfileViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

expect fun platformModule(): org.koin.core.module.Module

fun initKoin() = startKoin {
    modules(commonModule, platformModule(), networkModule)
}

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
val commonModule = module {
    viewModel { (navigator: HomeNavigator) ->
        HomeViewModel(get(), navigator)
    }
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

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single { Firebase.auth }

    single { Dispatchers.IO }

    factory {
        LoginGoogleUseCase(
            repository = get(),
            dispatcher = get()
        )
    }
    factory {
        GetUserUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    viewModel { (navigator: ProfileNavigator) ->
        ProfileViewModel(get(), get(), navigator)
    }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }

    single<SettingsLocalDataSource> { SettingsLocalDataSourceImpl(get()) }

    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }

    single { AuthService(get()) }
}