package dev.carlosivis.workoutsmart.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val navigator: ProfileNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: ProfileViewAction) {
        when (action) {
            ProfileViewAction.GoogleLogin -> onGoogleLoginClick()
            ProfileViewAction.Navigate.Back -> navigator.back()
            ProfileViewAction.Navigate.Settings -> navigator.toSettings()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    fun onGoogleLoginClick() {
        viewModelScope.launch {
            setLoading(true)
            loginGoogleUseCase(Unit)
                .onSuccess { userResponse ->
                    _state.update { it.copy(loginMessage = "Bem vindo ${userResponse.displayName}") }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            setLoading(false)
        }
    }
}
