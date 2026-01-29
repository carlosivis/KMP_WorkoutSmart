package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.models.UserResponse

interface AuthRepository {
    suspend fun loginWithGoogle(): Result<UserResponse>
}