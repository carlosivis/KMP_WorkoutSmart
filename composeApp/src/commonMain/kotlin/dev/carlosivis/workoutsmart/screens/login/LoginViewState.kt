package dev.carlosivis.workoutsmart.screens.login

data class LoginViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGoogleLoginEnabled: Boolean = true,
)