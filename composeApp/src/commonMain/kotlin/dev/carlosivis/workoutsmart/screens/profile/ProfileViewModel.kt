package dev.carlosivis.workoutsmart.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.domain.LogoutUseCase
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val navigator: ProfileNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())
    val state = _state.asStateFlow()

    init {
        getUser()
    }

    fun dispatchAction(action: ProfileViewAction) {
        when (action) {
            ProfileViewAction.GoogleLogin -> onGoogleLoginClick()
            ProfileViewAction.Navigate.Back -> navigator.back()
            ProfileViewAction.Navigate.Settings -> navigator.toSettings()
            ProfileViewAction.CleanError -> cleanError()
            ProfileViewAction.Logout -> logout()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun onGoogleLoginClick() {
        viewModelScope.launch {
            setLoading(true)
            loginGoogleUseCase(Unit)
                .onSuccess { userResponse ->
                    _state.update { it.copy(isLoading = false, user = userResponse) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            setLoading(true)
            getUserUseCase(Unit)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, user = it.user) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setLoading(true)
            logoutUseCase(Unit)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, user = null) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }

    private fun cleanError() {
        _state.update { it.copy(error = null) }
    }
}
