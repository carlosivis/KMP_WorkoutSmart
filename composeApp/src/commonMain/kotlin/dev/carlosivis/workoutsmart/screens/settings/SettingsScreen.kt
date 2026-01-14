package dev.carlosivis.workoutsmart.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
){
    val state by viewModel.state.collectAsState()
    val action: (SettingsViewAction) -> Unit = viewModel::dispatchAction

    Content(
        state = state,
        action = action
    )
    LaunchedEffect(Unit){
        action(SettingsViewAction.GetSettings)
    }
}

@Composable
fun Content(
    state: SettingsViewState,
    action: (SettingsViewAction) -> Unit
){

}