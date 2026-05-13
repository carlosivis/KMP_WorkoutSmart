package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.core.UseCase
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.shared.RankingMember
import kotlinx.coroutines.CoroutineDispatcher

class GetRankingMembersUseCase(
    private val repository: SocialRepository,
    dispatcher: CoroutineDispatcher
): UseCase<Int, List<RankingMember>>(dispatcher) {
    override suspend fun execute(params: Int): Result<List<RankingMember>> =
        repository.getRankingMembers(params)

}