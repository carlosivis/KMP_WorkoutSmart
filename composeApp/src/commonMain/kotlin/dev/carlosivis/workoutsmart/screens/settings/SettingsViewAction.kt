package dev.carlosivis.workoutsmart.screens.settings

import dev.carlosivis.workoutsmart.repository.ThemeMode

sealed class SettingsViewAction {
    object GetSettings : SettingsViewAction()
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsViewAction()
    data class UpdateDefaultRestTime(val time: Int) : SettingsViewAction()
    data class UpdateKeepScreenOn(val keepScreenOn: Boolean) : SettingsViewAction()
    data class UpdateVibrationEnabled(val vibrationEnabled: Boolean) : SettingsViewAction()
    object SaveSettings : SettingsViewAction()
    object NavigateBack : SettingsViewAction()
}
