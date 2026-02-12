package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.WorkoutModel

data class HomeNavigator(
    val toCreateWorkout: () -> Unit,
    val toActiveWorkout: (WorkoutModel) -> Unit,
    val toEditWorkout: (WorkoutModel) -> Unit,
    val toProfile: () -> Unit,
    val toGroups: (List<GroupResponse>?) -> Unit,
    val toRanking: (GroupResponse) -> Unit
)
