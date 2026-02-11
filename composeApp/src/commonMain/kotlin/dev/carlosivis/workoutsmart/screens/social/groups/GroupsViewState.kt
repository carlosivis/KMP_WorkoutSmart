package dev.carlosivis.workoutsmart.screens.social.groups

import dev.carlosivis.workoutsmart.models.GroupResponse

data class GroupsViewState(
    val isLoading: Boolean = false,
    val groups: List<GroupResponse>? = emptyList(),
    val error: String? = null
)
