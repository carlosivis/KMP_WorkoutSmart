package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.repository.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher

class CreateGroupUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<CreateGroupRequest, GroupResponse>(dispatcher) {

    override suspend fun execute(params: CreateGroupRequest): Result<GroupResponse> =
        repository.createGroup(params)
}