package dev.carlosivis.workoutsmart.screens.login

data class LoginViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGoogleLoginEnabled: Boolean = true,
    val loginMessage: String? = null
)