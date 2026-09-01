package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.shared.CreateGroupRequest
import dev.carlosivis.workoutsmart.shared.GroupResponse
import kotlinx.coroutines.CoroutineDispatcher

class CreateGroupUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<CreateGroupRequest, GroupResponse>(dispatcher) {

    override suspend fun execute(params: CreateGroupRequest): Result<GroupResponse> =
        repository.createGroup(params)
}