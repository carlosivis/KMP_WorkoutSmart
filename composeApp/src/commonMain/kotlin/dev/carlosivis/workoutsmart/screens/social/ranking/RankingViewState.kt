package dev.carlosivis.workoutsmart.screens.social.ranking

import dev.carlosivis.workoutsmart.shared.GroupResponse
import dev.carlosivis.workoutsmart.shared.RankingMember

data class RankingViewState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showInviteCode: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val podium: List<RankingMember> = emptyList(),
    val others: List<RankingMember> = emptyList(),
    val group: GroupResponse? = null
)
