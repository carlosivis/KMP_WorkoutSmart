package dev.carlosivis.workoutsmart.screens.social.groups

sealed class GroupsViewAction {
    object GetGroups : GroupsViewAction()
    object CleanError : GroupsViewAction()

    object Navigate {
        object Back : GroupsViewAction()
    }
}
