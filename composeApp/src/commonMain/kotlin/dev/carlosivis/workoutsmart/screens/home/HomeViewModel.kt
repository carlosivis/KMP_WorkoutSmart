package dev.carlosivis.workoutsmart.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.workoutsmart.domain.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.GetUserUseCase
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WorkoutRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val navigator: HomeNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()


    fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetWorkouts -> getWorkouts()
            is HomeViewAction.GetHistory -> getHistory()
            is HomeViewAction.Navigate.Details -> TODO()
            is HomeViewAction.Navigate.CreateWorkout -> navigator.toCreateWorkout()
            is HomeViewAction.Navigate.Workout -> navigator.toActiveWorkout(action.workout)
            is HomeViewAction.Navigate.Edit -> navigator.toEditWorkout(action.workout)
            is HomeViewAction.Navigate.Profile -> navigator.toProfile()
            is HomeViewAction.AttemptDeleteWorkout -> attemptDeleteWorkout(action.workout)
            is HomeViewAction.ConfirmDeleteWorkout -> deleteWorkout()
            is HomeViewAction.CancelDeleteWorkout -> cancelDeleteWorkout()
            is HomeViewAction.GetUserProfile -> getUser()
            is HomeViewAction.GetGroups -> getGroups()
            is HomeViewAction.CleanError -> cleanError()
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

    private fun attemptDeleteWorkout(workout: WorkoutModel) {
        _state.update { it.copy(workoutToDelete = workout) }
    }

    private fun cancelDeleteWorkout() {
        _state.update { it.copy(workoutToDelete = null) }
    }

    private fun deleteWorkout() {
        state.value.workoutToDelete?.let { workout ->
            viewModelScope.launch {
                setLoading(true)
                try {
                    repository.deleteWorkout(workout.id)
                } finally {
                    setLoading(false)
                    cancelDeleteWorkout()
                }
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            setLoading(true)
            getUserUseCase(Unit)
                .onSuccess { user ->
                    _state.update { it.copy(user = user) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            setLoading(false)
        }
    }

    private fun getGroups() {
        viewModelScope.launch {
            setLoading(true)
            getGroupsUseCase(Unit)
                .onSuccess { groups ->
                    _state.update { it.copy(groups = groups) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            setLoading(false)
        }
    }

    private fun cleanError(){
        _state.update { it.copy(error = null) }
    }
}