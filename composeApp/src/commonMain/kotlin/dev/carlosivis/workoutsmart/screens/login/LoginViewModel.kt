package dev.carlosivis.workoutsmart.screens.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
//    private val repository: AuthRepository,
): ViewModel() {
    private val _state = MutableStateFlow(LoginViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: LoginViewAction) {
        when (action) {
            LoginViewAction.GoogleLogin -> TODO()
            LoginViewAction.NavigateBack -> TODO()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

}