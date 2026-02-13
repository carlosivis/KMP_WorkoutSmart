package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.models.RankingMember
import dev.carlosivis.workoutsmart.screens.components.CustomTopBar
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.errorSnackbar

@Composable
fun RankingScreen(
    viewModel: RankingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (RankingViewAction) -> Unit = viewModel::dispatchAction

    Content(state, action)
}

@Composable
private fun Content(
    state: RankingViewState,
    action: (RankingViewAction) -> Unit,
) {
    val errorHandler = errorSnackbar(
        error = state.error,
        action = { action(RankingViewAction.CleanError) }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = errorHandler) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(Dimens.Medium)
        ) {
            CustomTopBar(
                onNavBackClick = {action(RankingViewAction.Navigate.Back)},
                title = state.group?.name
            )
        }
    }
}

@Composable
fun RankingPodiumCard(top3: List<RankingMember>){

}





