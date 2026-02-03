package dev.carlosivis.workoutsmart.screens.profile

import dev.carlosivis.workoutsmart.models.UserResponse

data class ProfileViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGoogleLoginEnabled: Boolean = true,
    val user: UserResponse? = null
)