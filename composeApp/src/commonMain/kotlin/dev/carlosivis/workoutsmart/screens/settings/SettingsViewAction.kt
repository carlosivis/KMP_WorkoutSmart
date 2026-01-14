package dev.carlosivis.workoutsmart.screens.settings

sealed class SettingsViewAction {
    object GetSettings : SettingsViewAction()
    object SaveSettings : SettingsViewAction()
    object NavigateBack : SettingsViewAction()
}


