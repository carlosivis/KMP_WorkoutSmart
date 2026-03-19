package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.models.GroupResponse

interface GroupsNavigator {
    fun toRanking(group: GroupResponse)
    fun back()
}
