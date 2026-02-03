package dev.carlosivis.workoutsmart.screens.profile

data class ProfileViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGoogleLoginEnabled: Boolean = true,
    val loginMessage: String? = null
)