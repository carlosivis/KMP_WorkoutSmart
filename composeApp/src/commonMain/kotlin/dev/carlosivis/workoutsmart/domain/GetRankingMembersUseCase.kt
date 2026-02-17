package dev.carlosivis.workoutsmart.domain

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.models.RankingMember
import dev.carlosivis.workoutsmart.repository.SocialRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetRankingMembersUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
): UseCase<Int, List<RankingMember>>(dispatcher) {
    override suspend fun execute(params: Int): Result<List<RankingMember>> =
        repository.getRankingMembers(params)

}