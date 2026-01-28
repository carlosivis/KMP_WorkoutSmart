package dev.carlosivis.workoutsmart.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.screens.components.GoogleButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (LoginViewAction) -> Unit = viewModel::dispatchAction

    Content(
        state = state,
        action = action
    )

}

@Composable
private fun Content(
    state: LoginViewState,
    action: (LoginViewAction) -> Unit
) {

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.Medium),
            verticalArrangement = Arrangement.Center
        ) {
            GoogleButton(
                enabled = !state.isLoading,
                onClick = { action(LoginViewAction.GoogleLogin) }
            )
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    Content(
        state = LoginViewState(),
        action = {}
    )
}