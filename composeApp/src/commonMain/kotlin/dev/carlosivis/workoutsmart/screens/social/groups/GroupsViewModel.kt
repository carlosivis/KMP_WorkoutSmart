package dev.carlosivis.workoutsmart.screens.social.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.CreateGroupUseCase
import dev.carlosivis.workoutsmart.domain.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.JoinGroupUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val groups: List<GroupResponse>? = null,
    private val GetGroupsUseCase: GetGroupsUseCase,
    private val CreateGroupUseCase: CreateGroupUseCase,
    private val JoinGroupUseCase: JoinGroupUseCase,
    private val navigator: GroupsNavigator
): ViewModel() {
    val _state = MutableStateFlow(GroupsViewState())
    val state = _state.asStateFlow()


    fun dispatchAction(action: GroupsViewAction) {
        when (action) {
            GroupsViewAction.GetGroups -> getGroups()
            GroupsViewAction.CleanError -> cleanError()
            GroupsViewAction.Navigate.Back -> navigator.back()
            is GroupsViewAction.Navigate.Ranking -> navigator.toRanking(action.id)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading)
    }

    private fun getGroups() {
        setLoading(true)
        if (groups != null) {
            _state.value = _state.value.copy(groups = groups)
        } else {
            viewModelScope.launch {
                GetGroupsUseCase(Unit)
                    .onSuccess { groups ->
                        _state.value = _state.value.copy(groups = groups)
                    }
                    .onFailure { error ->
                        _state.value = _state.value.copy(error = error.message)
                    }
            }
            setLoading(false)
        }
    }

    private fun cleanError(){
        _state.value = _state.value.copy(error = null)
    }
}