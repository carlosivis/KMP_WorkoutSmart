package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.usecase.GetRankingMembersUseCase
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
        getRanking()
    }

    fun dispatchAction(action: RankingViewAction) {
        when (action) {
            RankingViewAction.GetRanking -> getRanking()
            RankingViewAction.CleanMessages -> cleanMessages()
            RankingViewAction.Navigate.Back -> navigator.back()
            RankingViewAction.ShowInviteCode -> showInviteCode()
            RankingViewAction.CopyInviteCode -> copyInviteCode()
            RankingViewAction.Refresh -> getRanking()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun getRanking() {
        viewModelScope.launch {
            setLoading(true)
            GetRankingUseCase(group.id)
                .onSuccess { ranking ->
                    val sorted = ranking.sortedByDescending { it.score }
                    _state.update {
                        it.copy(
                            podium = sorted.take(3),
                            others = sorted.drop(3)
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            _state.update { it.copy(group = group) }
            setLoading(false)
        }
    }

    private fun showInviteCode() {
        _state.update { it.copy(showInviteCode = !it.showInviteCode) }
    }

    private fun copyInviteCode(){
        _state.update { it.copy(message = "Código copiado para a área de transferência!") }
    }

    private fun cleanMessages() {
        _state.update { it.copy(error = null, message = null) }
    }
}