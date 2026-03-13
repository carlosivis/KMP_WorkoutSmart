package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.models.GroupResponse

interface HomeNavigator {
    fun toCreateWorkout()
    fun toActiveWorkout(id: Long)
    fun toEditWorkout(id: Long)
    fun toProfile()
    fun toGroups(groups: List<GroupResponse>?)
    fun toRanking(group: GroupResponse)
}
