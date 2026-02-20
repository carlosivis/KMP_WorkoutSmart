package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.models.GroupResponse

data class HomeNavigator(
    val toCreateWorkout: () -> Unit,
    val toActiveWorkout: (id: Long) -> Unit,
    val toEditWorkout: (id: Long) -> Unit,
    val toProfile: () -> Unit,
    val toGroups: (List<GroupResponse>?) -> Unit,
    val toRanking: (GroupResponse) -> Unit
)
