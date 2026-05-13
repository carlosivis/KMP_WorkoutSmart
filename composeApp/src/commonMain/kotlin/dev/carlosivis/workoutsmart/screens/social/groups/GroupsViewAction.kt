package dev.carlosivis.workoutsmart.screens.social.groups

import dev.carlosivis.workoutsmart.shared.CreateGroupRequest
import dev.carlosivis.workoutsmart.shared.GroupResponse
import dev.carlosivis.workoutsmart.shared.JoinGroupRequest

sealed class GroupsViewAction {
    object Refresh : GroupsViewAction()
    object GetGroups : GroupsViewAction()
    object CleanMessages : GroupsViewAction()
    object ShowAddGroup : GroupsViewAction()
    object ShowAddInvite : GroupsViewAction()
    data class CreateGroup(val create: CreateGroupRequest) : GroupsViewAction()
    data class JoinGroup(val join: JoinGroupRequest) : GroupsViewAction()


    object Navigate {
        object Back : GroupsViewAction()
        class Ranking(val group: GroupResponse): GroupsViewAction()
    }
}
