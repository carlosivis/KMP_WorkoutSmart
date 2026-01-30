package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.screens.login.LoginViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LoginComponent(
    componentContext: ComponentContext,
    val onNavigateBack: () -> Unit,
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = LoginViewModel(
        loginGoogleUseCase = get(),
        onNavigateBack = onNavigateBack
    )
}