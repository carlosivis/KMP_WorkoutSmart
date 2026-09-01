package dev.carlosivis.workoutsmart.navigation.navigator

import dev.carlosivis.workoutsmart.shared.GroupResponse

interface GroupsNavigator {
    fun toRanking(group: GroupResponse)
    fun back()
}
