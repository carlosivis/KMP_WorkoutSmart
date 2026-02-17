package dev.carlosivis.workoutsmart.screens.social.ranking

sealed class RankingViewAction {
    object GetRanking : RankingViewAction()
    object CleanMessages : RankingViewAction()
    object ShowInviteCode : RankingViewAction()
    object CopyInviteCode : RankingViewAction()

    object Navigate {
        object Back : RankingViewAction()
    }
}
