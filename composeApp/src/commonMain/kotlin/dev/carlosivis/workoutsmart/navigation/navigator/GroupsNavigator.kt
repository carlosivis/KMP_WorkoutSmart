package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.models.GroupResponse

data class GroupsNavigator(
    val toRanking: (group: GroupResponse) -> Unit,
    val back: () -> Unit,
)
