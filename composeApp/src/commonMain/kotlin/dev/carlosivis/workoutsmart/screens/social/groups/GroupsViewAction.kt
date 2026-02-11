package dev.carlosivis.workoutsmart.screens.social.groups

import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.JoinGroupRequest

sealed class GroupsViewAction {
    object GetGroups : GroupsViewAction()
    object CleanError : GroupsViewAction()
    object ShowAddGroup : GroupsViewAction()
    object ShowAddInvite : GroupsViewAction()
    data class CreateGroup(val create: CreateGroupRequest) : GroupsViewAction()
    data class JoinGroup(val join: JoinGroupRequest) : GroupsViewAction()


    object Navigate {
        object Back : GroupsViewAction()
        class Ranking(val id: Int): GroupsViewAction()
    }
}
