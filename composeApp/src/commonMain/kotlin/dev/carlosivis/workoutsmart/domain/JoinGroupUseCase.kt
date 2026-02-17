package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.repository.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher

class JoinGroupUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<JoinGroupRequest, GroupResponse>(
    dispatcher
) {
    override suspend fun execute(params: JoinGroupRequest): Result<GroupResponse> =
        repository.joinGroup(params)
}