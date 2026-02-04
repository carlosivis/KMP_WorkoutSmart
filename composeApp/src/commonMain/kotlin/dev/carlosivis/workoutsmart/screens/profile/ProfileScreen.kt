package dev.carlosivis.workoutsmart.screens.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.delete_action
import dev.carlosivis.workoutsmart.composeResources.ic_user_placeholder
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.GoogleButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (ProfileViewAction) -> Unit = viewModel::dispatchAction
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
            action(ProfileViewAction.CleanError)
        }
    }
    LaunchedEffect(Unit) {
        action(ProfileViewAction.GetUserProfile)
    }

    Content(
        state = state,
        action = action,
        snackbarHostState = snackbarHostState
    )

}

@Composable
private fun Content(
    state: ProfileViewState,
    action: (ProfileViewAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    AnimatedVisibility(
        visible = state.isLoading,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    Box(Modifier.fillMaxSize()) {

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Dimens.Medium),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { action(ProfileViewAction.Navigate.Back) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }

                    IconButton(
                        onClick = { action(ProfileViewAction.Navigate.Settings) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            stringResource(Res.string.delete_action)
                        )
                    }
                }
                AnimatedContent(
                    targetState = state.user,
                    transitionSpec = {
                       if (initialState == null || targetState == null) {
                            (fadeIn(animationSpec = tween(600)) +
                                    slideInHorizontally { width -> width / 2 })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(600)) +
                                            slideOutHorizontally { width -> -width / 2 })
                        } else {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        }
                    },
                    label = "ProfileTransition"
                ) { currentUser ->

                    if (currentUser == null) {
                        LoginSection(
                            onLoginClick = { action(ProfileViewAction.GoogleLogin) }
                        )
                    } else {
                        ProfileSection(
                            user = currentUser, // Passa o objeto seguro
                            logout = { action(ProfileViewAction.Logout) }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(user: UserResponse, logout: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Dimens.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(Dimens.ImageSizeLarge),
            shape = CircleShape,
            shadowElevation = Dimens.Medium
        ) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
                    .clip(CircleShape)
                    .border(Dimens.ExtraSmall, MaterialTheme.colorScheme.primary, CircleShape),
                placeholder = painterResource(Res.drawable.ic_user_placeholder),
                error = painterResource(Res.drawable.ic_user_placeholder),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(Dimens.Medium))

        Text(
            text = user.displayName ?: "Atleta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Dimens.Large))

        TextButton(
            onClick = { logout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(Dimens.Medium),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(Dimens.Small))
            Text(
                text = "Sair da conta",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LoginSection(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Dimens.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(Dimens.ImageSizeLarge),
            shape = CircleShape,
            shadowElevation = Dimens.Medium
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.padding(Dimens.Medium)
                    .fillMaxSize()
            )
        }
        Spacer(Modifier.height(Dimens.Large))
        Text(
            text = "Evolua seu Treino",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sincronize seu progresso e pontue seus treinos em qualquer dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(Dimens.ExtraLarge))
        GoogleButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onLoginClick() }
        )
    }
}

@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = ProfileViewState(),
            action = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }

}