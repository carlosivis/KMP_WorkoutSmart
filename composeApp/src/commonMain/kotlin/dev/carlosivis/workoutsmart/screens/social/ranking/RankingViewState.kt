package dev.carlosivis.workoutsmart.screens.social.ranking

import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.RankingMember

data class RankingViewState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val ranking: List<RankingMember> = emptyList(),
    val group: GroupResponse? = null
)
