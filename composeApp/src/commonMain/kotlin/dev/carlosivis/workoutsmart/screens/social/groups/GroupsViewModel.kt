package dev.carlosivis.workoutsmart.screens.social.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(): ViewModel() {
    val _state = MutableStateFlow(GroupsViewState())
    val state = _state.asStateFlow()


    fun dispatchAction(action: GroupsViewAction) {
        when (action) {
            GroupsViewAction.GetGroups -> getGroups()
            GroupsViewAction.CleanError -> TODO()
            GroupsViewAction.Navigate.Back -> TODO()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading)
    }

    private fun getGroups(){
        setLoading(true)
        viewModelScope.launch {

        }
        setLoading(false)
    }
}