package dev.carlosivis.workoutsmart.screens.settings

import dev.carlosivis.workoutsmart.models.SettingsModel

data class SettingsViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val settings: SettingsModel = SettingsModel.default()
)

