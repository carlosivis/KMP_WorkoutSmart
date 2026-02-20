package dev.carlosivis.workoutsmart.platform

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.copyText(text: String)

expect suspend fun shareText(text: String)