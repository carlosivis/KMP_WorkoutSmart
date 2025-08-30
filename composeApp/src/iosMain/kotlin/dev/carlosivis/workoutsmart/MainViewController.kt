package dev.carlosivis.workoutsmart

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.carlosivis.workoutsmart.navigation.RootComponent

@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController() = ComposeUIViewController {
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(
        componentContext = DefaultComponentContext(lifecycle)
    )
    App(root)
}
