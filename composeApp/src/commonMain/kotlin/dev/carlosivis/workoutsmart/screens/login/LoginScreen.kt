package dev.carlosivis.workoutsmart.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.delete_action
import dev.carlosivis.workoutsmart.screens.components.GoogleButton
import org.jetbrains.compose.resources.stringResource

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
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                IconButton(
                    onClick = { action(LoginViewAction.Navigate.Back) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_back)
                    )
                }

                IconButton(
                    onClick = { action(LoginViewAction.Navigate.Settings) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Filled.Settings,
                        stringResource(Res.string.delete_action))
                }
            }
            GoogleButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
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