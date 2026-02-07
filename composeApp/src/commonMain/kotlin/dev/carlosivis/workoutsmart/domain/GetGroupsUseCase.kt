package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.repository.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetGroupsUseCase (
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
): UseCase<Unit, List<GroupResponse>>(dispatcher) {
    override suspend fun execute(params: Unit): Result<List<GroupResponse>> {
        return repository.getGroups()
    }
}
