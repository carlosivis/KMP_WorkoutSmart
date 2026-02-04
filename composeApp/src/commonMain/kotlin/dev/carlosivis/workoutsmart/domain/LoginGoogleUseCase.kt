package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher

class LoginGoogleUseCase(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, UserResponse>(dispatcher) {

    override suspend fun execute(params: Unit): Result<UserResponse> {
        return repository.loginWithGoogle()
    }
}