package dev.carlosivis.workoutsmart.screens.social.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    //private val GetRankingUseCase: GetRankingUseCase,
    private val navigator: RankingNavigator
): ViewModel() {

    private val _state = MutableStateFlow(RankingViewState())
    val state = _state.asStateFlow()

    fun dispatchAction(action: RankingViewAction) {
        when (action) {
            RankingViewAction.GetRanking -> getRanking()
            RankingViewAction.CleanError -> cleanError()
            RankingViewAction.Navigate.Back -> TODO()
        }
    }
    private fun setLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading)
    }

    private fun getRanking() {
        setLoading(true)
        viewModelScope.launch {

        }
        setLoading(false)
    }

    private fun cleanError(){
        _state.value = _state.value.copy(error = null)
    }
}