package dev.carlosivis.workoutsmart.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.domain.repository.WorkoutRepository
import dev.carlosivis.workoutsmart.domain.usecase.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.usecase.RegisterWorkoutLogUseCase
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WorkoutRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val registerWorkoutLogUseCase: RegisterWorkoutLogUseCase,
    private val navigator: HomeNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = combine(
        _state,
        repository.getAllWorkouts(),
        repository.getAllHistory()
    ) { currentState, workouts, history ->
        currentState.copy(
            workouts = workouts,
            history = history
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeViewState(isLoading = true)
    )

    init {
        getUser()
        getGroups()
    }

    fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.Navigate.CreateWorkout -> navigator.toCreateWorkout()
            is HomeViewAction.Navigate.Workout -> navigator.toActiveWorkout(action.id)
            is HomeViewAction.Navigate.Edit -> navigator.toEditWorkout(action.id)
            is HomeViewAction.Navigate.Profile -> navigator.toProfile()
            is HomeViewAction.AttemptDeleteWorkout -> attemptDeleteWorkout(action.id, action.name)
            is HomeViewAction.ConfirmDeleteWorkout -> deleteWorkout()
            is HomeViewAction.CancelDeleteWorkout -> cancelDeleteWorkout()
            is HomeViewAction.GetUserProfile -> getUser()
            is HomeViewAction.GetGroups -> getGroups()
            is HomeViewAction.CleanMessages -> cleanMessages()
            is HomeViewAction.Navigate.Groups -> navigator.toGroups(_state.value.groups)
            is HomeViewAction.Navigate.Ranking -> navigator.toRanking(action.group)
            is HomeViewAction.RegisterWorkoutLog -> registerWorkoutLog(action.log)
            is HomeViewAction.ShowRegisterWorkoutDialog -> showRegisterWorkoutDialog()
            is HomeViewAction.Refresh -> refresh()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun attemptDeleteWorkout(id: Long, name: String) {
        _state.update { it.copy(workoutIdToDelete = id, workoutToDelete = name) }
    }

    private fun cancelDeleteWorkout() {
        _state.update { it.copy(workoutIdToDelete = null, workoutToDelete = null) }
    }

    private fun deleteWorkout() {
        _state.value.workoutIdToDelete?.let { id ->
            viewModelScope.launch {
                setLoading(true)
                try {
                    repository.deleteWorkout(id)
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message) }
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

    private fun showRegisterWorkoutDialog() {
        _state.update { it.copy(showRegisterWorkoutDialog = !it.showRegisterWorkoutDialog) }
    }


    private fun registerWorkoutLog(workout: WorkoutLogRequest) {
        viewModelScope.launch {
            setLoading(true)
            registerWorkoutLogUseCase(workout)
                .onSuccess {
                    _state.update { it.copy(message ="Workout saved successfully") }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            showRegisterWorkoutDialog()
            setLoading(false)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setLoading(true)
            _state.update { it.copy(isRefreshing = true) }

            awaitAll(
                async {
                    getGroupsUseCase(Unit)
                        .onSuccess { groups ->
                            _state.update { it.copy(groups = groups) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(error = error.message) }
                        }
                },
                async {
                    getUserUseCase(Unit)
                        .onSuccess { user ->
                            _state.update { it.copy(user = user) }
                        }
                        .onFailure { error ->
                            _state.update { it.copy(error = error.message) }
                        }
                })
            setLoading(false)
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun cleanMessages() {
        _state.update { it.copy(error = null, message = null) }
    }
}