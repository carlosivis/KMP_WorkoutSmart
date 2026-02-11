package dev.carlosivis.workoutsmart.navigation.navigator

data class GroupsNavigator(
    val toRanking: (id: Int) -> Unit,
    val back: () -> Unit,
)
