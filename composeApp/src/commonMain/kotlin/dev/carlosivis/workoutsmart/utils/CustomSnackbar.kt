package dev.carlosivis.workoutsmart.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


enum class SnackbarType {
    ERROR, SUCCESS
}

@Composable
fun rememberSnackbarHandler(
    error: String?,
    message: String?,
    action: () -> Unit,
    duration: SnackbarDuration = SnackbarDuration.Short
): Pair<SnackbarHostState, SnackbarType> {
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.SUCCESS) }

    LaunchedEffect(error) {
        error?.let {
            snackbarType = SnackbarType.ERROR
            snackbarHostState.showSnackbar(message = it, duration = duration)
            action()
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarType = SnackbarType.SUCCESS
            snackbarHostState.showSnackbar(message = it, duration = duration)
            action()
        }
    }

    return Pair(snackbarHostState, snackbarType)
}

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    type: SnackbarType,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        val containerColor = when (type) {
            SnackbarType.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            SnackbarType.SUCCESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        }
        val contentColor = when (type) {
            SnackbarType.ERROR -> MaterialTheme.colorScheme.onError
            SnackbarType.SUCCESS -> MaterialTheme.colorScheme.onPrimary
        }

        Snackbar(
            snackbarData = data,
            containerColor = containerColor,
            contentColor = contentColor,
            shape = MaterialTheme.shapes.extraLarge
        )
    }
}