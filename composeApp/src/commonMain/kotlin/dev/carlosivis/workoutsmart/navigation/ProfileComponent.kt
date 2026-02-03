package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.screens.profile.ProfileViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ProfileComponent(
    componentContext: ComponentContext,
    val navigator: ProfileNavigator,
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = ProfileViewModel(
        loginGoogleUseCase = get(),
        getUserUseCase = get(),
        navigator = navigator
    )
}