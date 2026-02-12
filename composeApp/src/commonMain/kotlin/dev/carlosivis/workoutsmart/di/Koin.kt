package dev.carlosivis.workoutsmart.di

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.BuildConfig
import dev.carlosivis.workoutsmart.core.KtorClient
import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSourceImpl
import dev.carlosivis.workoutsmart.data.local.datasource.UserLocalDataSource
import dev.carlosivis.workoutsmart.data.local.datasource.UserLocalDataSourceImpl
import dev.carlosivis.workoutsmart.data.remote.datasource.AuthRemoteDataSource
import dev.carlosivis.workoutsmart.data.remote.datasource.AuthRemoteDataSourceImpl
import dev.carlosivis.workoutsmart.data.remote.datasource.SocialRemoteDataSource
import dev.carlosivis.workoutsmart.data.remote.datasource.SocialRemoteDataSourceImpl
import dev.carlosivis.workoutsmart.data.remote.service.AuthService
import dev.carlosivis.workoutsmart.data.remote.service.SocialService
import dev.carlosivis.workoutsmart.database.DatabaseHelper
import dev.carlosivis.workoutsmart.domain.CreateGroupUseCase
import dev.carlosivis.workoutsmart.domain.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.JoinGroupUseCase
import dev.carlosivis.workoutsmart.domain.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.domain.LogoutUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.repository.AuthRepository
import dev.carlosivis.workoutsmart.repository.AuthRepositoryImpl
import dev.carlosivis.workoutsmart.repository.SettingsRepository
import dev.carlosivis.workoutsmart.repository.SettingsRepositoryImpl
import dev.carlosivis.workoutsmart.repository.SocialRepository
import dev.carlosivis.workoutsmart.repository.SocialRepositoryImpl
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.repository.WorkoutRepositoryImpl
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import dev.carlosivis.workoutsmart.screens.profile.ProfileViewModel
import dev.carlosivis.workoutsmart.screens.settings.SettingsViewModel
import dev.carlosivis.workoutsmart.screens.social.groups.GroupsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

expect fun platformModule(): Module

val networkModule = module {

    single { Firebase.auth }

    single {
        KtorClient(
            baseUrl = BuildConfig.BASE_URL,
            firebaseAuth = get()
        )
    }
    single { AuthService(get()) }

    single { SocialService(get()) }
}

@OptIn(ExperimentalTime::class)
val commonModule = module {
    viewModel { (navigator: HomeNavigator) ->
        HomeViewModel(get(), get(), get(), navigator)
    }
    viewModel { (workout: WorkoutModel, onNavigateBack: () -> Unit) ->
        ActiveWorkoutViewModel(workout, get(), get(), onNavigateBack, get())
    }
    viewModel { (onNavigateBack: () -> Unit) ->
        CreateWorkoutViewModel(get(), onNavigateBack)
    }

    viewModel { (navigator: GroupsNavigator, groups: List<GroupResponse>?) ->
        GroupsViewModel(
           groups, get(), get(), get(), navigator
        )
    }

    viewModel {(onNavigateBack: () -> Unit) ->
        SettingsViewModel(get(), onNavigateBack)
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

    single<Settings> { Settings() }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

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

    factory {
        LogoutUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    factory {
        GetGroupsUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    factory {
        JoinGroupUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    factory {
        CreateGroupUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    viewModel { (navigator: ProfileNavigator) ->
        ProfileViewModel(get(), get(), get(), navigator)
    }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }

    single<SettingsLocalDataSource> { SettingsLocalDataSourceImpl(get()) }

    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }

    single<SocialRemoteDataSource> { SocialRemoteDataSourceImpl(get()) }

    single<SocialRepository> { SocialRepositoryImpl(get()) }

}