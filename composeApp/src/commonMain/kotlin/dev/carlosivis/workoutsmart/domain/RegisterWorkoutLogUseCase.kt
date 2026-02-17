package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.repository.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher

class RegisterWorkoutLogUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
): UseCase<WorkoutLogRequest, Unit>(dispatcher) {

    override suspend fun execute(params: WorkoutLogRequest): Result<Unit> =
        repository.registerWorkoutLog(params)

}