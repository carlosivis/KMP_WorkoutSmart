package dev.carlosivis.workoutsmart.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.carlosivis.workoutsmart.Utils.WorkoutsSmartTheme

@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    WorkoutsSmartTheme(true) {

        // HomeScreen() { }
    }
}
@Preview(showSystemUi = true, uiMode = 1)
@Composable
fun PreviewCreateWorkoutScreen() {
    WorkoutsSmartTheme(false) {

        // CreateWorkoutScreen() { }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewActiveWorkoutScreen() {
    WorkoutsSmartTheme(true) {

    // ActiveWorkoutScreen() { }
    }
}
