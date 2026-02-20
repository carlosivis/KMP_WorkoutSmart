package dev.carlosivis.workoutsmart.di

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.BuildConfig
import dev.carlosivis.workoutsmart.core.KtorClient
import dev.carlosivis.workoutsmart.data.local.DatabaseHelper
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
import dev.carlosivis.workoutsmart.domain.usecase.CreateGroupUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetRankingMembersUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.usecase.JoinGroupUseCase
import dev.carlosivis.workoutsmart.domain.usecase.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.domain.usecase.LogoutUseCase
import dev.carlosivis.workoutsmart.domain.usecase.RegisterWorkoutLogUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import dev.carlosivis.workoutsmart.data.repository.AuthRepositoryImpl
import dev.carlosivis.workoutsmart.domain.repository.SettingsRepository
import dev.carlosivis.workoutsmart.data.repository.SettingsRepositoryImpl
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.data.repository.SocialRepositoryImpl
import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.data.repository.WorkoutRepositoryImpl
import dev.carlosivis.workoutsmart.screens.activeWorkout.ActiveWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.createWorkout.CreateWorkoutViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import dev.carlosivis.workoutsmart.screens.profile.ProfileViewModel
import dev.carlosivis.workoutsmart.screens.settings.SettingsViewModel
import dev.carlosivis.workoutsmart.screens.social.groups.GroupsViewModel
import dev.carlosivis.workoutsmart.screens.social.ranking.RankingViewModel
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

fun appModules() = listOf(
    networkModule,
    dataModule,
    domainModule,
    viewmodelModule,
    coreModule,
    platformModule()
)

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

val dataModule = module {
    single<SettingsLocalDataSource> { SettingsLocalDataSourceImpl(get()) }
    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<SocialRemoteDataSource> { SocialRemoteDataSourceImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<SocialRepository> { SocialRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }
}

val domainModule = module {
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

    factory {
        GetRankingMembersUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    factory {
        RegisterWorkoutLogUseCase(
            repository = get(),
            dispatcher = get()
        )
    }
}

val viewmodelModule = module {
    viewModel { (navigator: HomeNavigator) ->
        HomeViewModel(get(), get(), get(), get(), navigator)
    }

    viewModel { (workoutId: Long, onNavigateBack: () -> Unit) ->
        ActiveWorkoutViewModel(workoutId, get(), get(), get(), onNavigateBack, get())
    }

    viewModel { (onNavigateBack: () -> Unit, workoutIdToEdit: Long?) ->
        CreateWorkoutViewModel(get(), onNavigateBack, workoutIdToEdit)
    }

    viewModel { (navigator: GroupsNavigator, groups: List<GroupResponse>?) ->
        GroupsViewModel(
            groups, get(), get(), get(), navigator
        )
    }

    viewModel { (navigator: RankingNavigator, group: GroupResponse) ->
        RankingViewModel(group, get(), navigator)
    }

    viewModel { (navigator: ProfileNavigator) ->
        ProfileViewModel(get(), get(), get(), navigator)
    }

    viewModel { (onNavigateBack: () -> Unit) ->
        SettingsViewModel(get(), onNavigateBack)
    }
}

@OptIn(ExperimentalTime::class)
val coreModule = module {
    single {
        DatabaseHelper(get(), Dispatchers.Default)
    }

    single<Settings> { Settings() }

    single { Dispatchers.IO }

    single<Clock> {
        Clock.System
    }
}