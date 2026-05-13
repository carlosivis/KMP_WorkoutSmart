package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.shared.GroupResponse
import kotlinx.coroutines.CoroutineDispatcher

class GetGroupsUseCase (
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
): UseCase<Unit, List<GroupResponse>>(dispatcher) {
    override suspend fun execute(params: Unit): Result<List<GroupResponse>> {
        return repository.getGroups()
    }
}
