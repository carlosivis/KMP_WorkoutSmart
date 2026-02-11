package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import dev.carlosivis.workoutsmart.screens.social.ranking.RankingViewModel
import org.koin.core.component.KoinComponent

class RankingComponent(
    componentContext: ComponentContext,
    val navigator: RankingNavigator
): ComponentContext by componentContext, KoinComponent {
    val viewModel = RankingViewModel(
        navigator = navigator
    )
}