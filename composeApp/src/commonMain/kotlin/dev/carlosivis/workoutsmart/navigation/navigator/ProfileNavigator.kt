package dev.carlosivis.workoutsmart.navigation.navigator

data class ProfileNavigator(
    val toSettings: () -> Unit,
    val back: () -> Unit,
)