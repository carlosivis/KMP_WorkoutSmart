package dev.carlosivis.workoutsmart.screens.social.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.CreateGroupUseCase
import dev.carlosivis.workoutsmart.domain.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.JoinGroupUseCase
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val groups: List<GroupResponse>? = null,
    private val GetGroupsUseCase: GetGroupsUseCase,
    private val CreateGroupUseCase: CreateGroupUseCase,
    private val JoinGroupUseCase: JoinGroupUseCase,
    private val navigator: GroupsNavigator
) : ViewModel() {
    val _state = MutableStateFlow(GroupsViewState())
    val state = _state.asStateFlow()


    fun dispatchAction(action: GroupsViewAction) {
        when (action) {
            GroupsViewAction.GetGroups -> getGroups()
            GroupsViewAction.CleanError -> cleanError()
            GroupsViewAction.Navigate.Back -> navigator.back()
            is GroupsViewAction.Navigate.Ranking -> navigator.toRanking(action.group)
            is GroupsViewAction.CreateGroup -> createGroup(action.create)
            is GroupsViewAction.JoinGroup -> joinGroup(action.join)
            GroupsViewAction.ShowAddGroup -> showAddGroup()
            GroupsViewAction.ShowAddInvite -> showAddInvite()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading)}
    }

    private fun getGroups() {
        setLoading(true)
        if (groups != null) {
            _state.update { it.copy(groups = groups)}
        } else {
            viewModelScope.launch {
                GetGroupsUseCase(Unit)
                    .onSuccess { groups ->
                        _state.update { it.copy(groups = groups)}
                    }
                    .onFailure { error ->
                        _state.update { it.copy(error = error.message)}
                    }
            }
        }
        setLoading(false)
    }

    private fun createGroup(params: CreateGroupRequest) {
        setLoading(true)
        showAddGroup()
        viewModelScope.launch {
            CreateGroupUseCase(params)
                .onSuccess { group ->
                    _state.update {  _state.value.copy(
                        groups = _state.value.groups?.plus(group)
                    )}
                    navigator.toRanking(group)
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message)}
                }
        }
        setLoading(false)
    }

    private fun joinGroup(params: JoinGroupRequest) {
        setLoading(true)
        showAddInvite()
        viewModelScope.launch {
            JoinGroupUseCase(params)
                .onSuccess { group ->
                    _state.update {
                        it.copy(
                            groups = it.groups?.plus(group)
                        )
                    }
                    navigator.toRanking(group)
                }
                .onFailure { error ->
                    _state.update {  it.copy(error = error.message)}
                }
        }
        setLoading(false)
    }

    private fun showAddGroup() {
        _state.update {
            it.copy(showAddGroup = !it.showAddGroup)
        }
    }

    private fun showAddInvite() {
        _state.update {
            it.copy(showAddInvite = !it.showAddInvite)
        }
    }


    private fun cleanError() {
        _state.update { it.copy(error = null) }
    }
}