package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.screens.settings.SettingsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class SettingsComponent(
    componentContext: ComponentContext,
    val onNavigateBack: () -> Unit,
): ComponentContext by componentContext, KoinComponent {
    val viewModel: SettingsViewModel = get { parametersOf(onNavigateBack) }
}