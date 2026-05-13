package dev.carlosivis.workoutsmart.domain.repository

import dev.carlosivis.workoutsmart.shared.UserResponse

interface AuthRepository {
    suspend fun loginWithGoogle(): Result<UserResponse>
    suspend fun logout(): Result<Unit>
    suspend fun getUser(): Result<UserResponse?>
}