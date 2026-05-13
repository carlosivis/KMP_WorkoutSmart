package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.shared.GroupResponse
import dev.carlosivis.workoutsmart.shared.JoinGroupRequest
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