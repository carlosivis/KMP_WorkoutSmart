package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.GetRankingMembersUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val group: GroupResponse,
    private val GetRankingUseCase: GetRankingMembersUseCase,
    private val navigator: RankingNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(RankingViewState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(group = group) }
        getRanking()
    }

    fun dispatchAction(action: RankingViewAction) {
        when (action) {
            RankingViewAction.GetRanking -> getRanking()
            RankingViewAction.CleanError -> cleanError()
            RankingViewAction.Navigate.Back -> navigator.back()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun getRanking() {
        setLoading(true)
        viewModelScope.launch {
            GetRankingUseCase(_state.value.group!!.id)
                .onSuccess { ranking ->
                    _state.update { it.copy(ranking = ranking) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
        setLoading(false)
    }

    private fun cleanError() {
        _state.update { it.copy(error = null) }
    }
}