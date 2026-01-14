package dev.carlosivis.workoutsmart.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.repository.SettingsRepository
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
            is SettingsViewAction.SaveSettings -> saveSettings()
            is SettingsViewAction.NavigateBack -> onNavigateBack()
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

    private fun saveSettings(){

    }



}