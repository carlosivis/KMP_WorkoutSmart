package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.carlosivis.workoutsmart.screens.profile.ProfileViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class ProfileComponent(
    componentContext: ComponentContext,
    val navigator: ProfileNavigator,
) : ComponentContext by componentContext, KoinComponent {
    val viewModel: ProfileViewModel = get { parametersOf(navigator) }
}