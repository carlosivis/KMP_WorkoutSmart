package dev.carlosivis.workoutsmart.screens.social.ranking

sealed class RankingViewAction {
    object GetRanking : RankingViewAction()
    object CleanError : RankingViewAction()

    object Navigate {
        object Back : RankingViewAction()
    }
}
