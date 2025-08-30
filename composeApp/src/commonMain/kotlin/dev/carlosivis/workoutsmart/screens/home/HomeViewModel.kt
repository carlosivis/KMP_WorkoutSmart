package dev.carlosivis.workoutsmart.screens.home

import androidx.lifecycle.ViewModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WorkoutRepository,
    private val onNavigateToCreateWorkout: () -> Unit,
    private val onNavigateToWorkout: (WorkoutModel) -> Unit
): ViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()


    fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetWorkouts -> getWorkouts()
            is HomeViewAction.GetHistory -> getHistory()
            is HomeViewAction.Navigate.Details -> TODO()
            is HomeViewAction.Navigate.CreateWorkout -> onNavigateToCreateWorkout()
            is HomeViewAction.Navigate.Workout -> onNavigateToWorkout(action.workout)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun getWorkouts() {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.getAllWorkouts()
                    .collect { workouts ->
                        _state.update { it.copy(workouts = workouts) }
                    }
            } finally {
                setLoading(false)
            }
        }
    }
    private fun getHistory() {
        viewModelScope.launch {
            setLoading(true)
            try {
                repository.getAllHistory()
                    .collect { history ->
                        _state.update { it.copy(history = history) }
                    }
            } finally {
                setLoading(false)
            }
        }
    }

}