package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import dev.carlosivis.workoutsmart.screens.social.ranking.RankingViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class RankingComponent(
    componentContext: ComponentContext,
    val navigator: RankingNavigator,
    val group: GroupResponse
) : ComponentContext by componentContext, KoinComponent {
    val viewModel: RankingViewModel = get { parametersOf(navigator, group) }
}