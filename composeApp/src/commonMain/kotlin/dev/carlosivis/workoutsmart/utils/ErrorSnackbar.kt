package dev.carlosivis.workoutsmart.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember


@Composable
fun errorSnackbar(
    error: String?,
    action: () -> Unit,
    duration: SnackbarDuration = SnackbarDuration.Short
): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = duration
            )
            action()
        }
    }

    return snackbarHostState
}
