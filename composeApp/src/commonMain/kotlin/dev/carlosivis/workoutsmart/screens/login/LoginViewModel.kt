package dev.carlosivis.workoutsmart.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.LoginGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val onNavigateBack: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(LoginViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: LoginViewAction) {
        when (action) {
            LoginViewAction.GoogleLogin -> onGoogleLoginClick()
            LoginViewAction.NavigateBack -> onNavigateBack()
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
