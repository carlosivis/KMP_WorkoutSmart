package dev.carlosivis.workoutsmart.screens.settings

import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.repository.ThemeMode

sealed class SettingsViewAction {
    object GetSettings : SettingsViewAction()
    data class UpdateSettings(val settings: SettingsModel) : SettingsViewAction()
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsViewAction()
    object SaveSettings : SettingsViewAction()
    object NavigateBack : SettingsViewAction()
}
