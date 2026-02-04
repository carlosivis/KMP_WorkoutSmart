package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeComponent(
    componentContext: ComponentContext,
    val navigator: HomeNavigator
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = HomeViewModel(
        repository = get(),
        getUserUseCase = get(),
        navigator = navigator
    )
}
