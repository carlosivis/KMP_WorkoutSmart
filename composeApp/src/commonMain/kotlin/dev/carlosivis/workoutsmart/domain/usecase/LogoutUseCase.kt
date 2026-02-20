package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher

class LogoutUseCase(
    private val repository: AuthRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, Unit>(dispatcher) {
    override suspend fun execute(params: Unit) =
        repository.logout()
}