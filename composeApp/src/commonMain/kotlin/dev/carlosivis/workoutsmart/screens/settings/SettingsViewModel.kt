package dev.carlosivis.workoutsmart.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.repository.SettingsRepository
import dev.carlosivis.workoutsmart.repository.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val onNavigateBack: () -> Unit
): ViewModel() {
    private val _state = MutableStateFlow(SettingsViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: SettingsViewAction) {
        when (action) {
            is SettingsViewAction.GetSettings -> getSettings()
            is SettingsViewAction.UpdateSettings -> updateSettings(action.settings)
            is SettingsViewAction.SaveSettings -> saveSettings()
            is SettingsViewAction.NavigateBack -> onNavigateBack()
            is SettingsViewAction.UpdateThemeMode -> updateThemeMode(action.themeMode)
        }
    }

    private fun getSettings(){
        viewModelScope.launch {
                repository.getSettings()
                    .collect { settings ->
                        _state.update { it.copy(settings = settings) }
                    }
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode){
        _state.update {
            it.copy(
                settings = it.settings.copy(
                    themeMode = themeMode
                )
            )
        }
        saveSettings()
    }

    private fun updateSettings(settings: SettingsModel) {
        _state.update {
            it.copy(
                settings = it.settings.copy(
                    themeMode = settings.themeMode,
                    defaultRestSeconds = settings.defaultRestSeconds,
                    keepScreenOn = settings.keepScreenOn,
                    vibrationEnabled = settings.vibrationEnabled
                )
            )
        }
    }

    private fun saveSettings(){
        viewModelScope.launch {
            repository.saveSettings(_state.value.settings)
        }
    }

}