package dev.carlosivis.workoutsmart.screens.profile

import dev.carlosivis.workoutsmart.shared.UserResponse

data class ProfileViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isGoogleLoginEnabled: Boolean = true,
    val user: UserResponse? = null
)