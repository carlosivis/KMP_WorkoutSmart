package dev.carlosivis.workoutsmart.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.workout_saved_successfully
import dev.carlosivis.workoutsmart.domain.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.RegisterWorkoutLogUseCase
import dev.carlosivis.workoutsmart.navigation.navigator.HomeNavigator
import dev.carlosivis.workoutsmart.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class HomeViewModel(
    private val repository: WorkoutRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val registerWorkoutLogUseCase: RegisterWorkoutLogUseCase,
    private val navigator: HomeNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    init {
        getWorkouts()
        getHistory()
        getUser()
        getGroups()
    }

    fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetWorkouts -> getWorkouts()
            is HomeViewAction.GetHistory -> getHistory()
            is HomeViewAction.Navigate.CreateWorkout -> navigator.toCreateWorkout()
            is HomeViewAction.Navigate.Workout -> navigator.toActiveWorkout(action.id)
            is HomeViewAction.Navigate.Edit -> navigator.toEditWorkout(action.id)
            is HomeViewAction.Navigate.Profile -> navigator.toProfile()
            is HomeViewAction.AttemptDeleteWorkout -> attemptDeleteWorkout(action.id,action.name)
            is HomeViewAction.ConfirmDeleteWorkout -> deleteWorkout()
            is HomeViewAction.CancelDeleteWorkout -> cancelDeleteWorkout()
            is HomeViewAction.GetUserProfile -> getUser()
            is HomeViewAction.GetGroups -> getGroups()
            is HomeViewAction.CleanMessages -> cleanMessages()
            is HomeViewAction.Navigate.Groups -> navigator.toGroups(_state.value.groups)
            is HomeViewAction.Navigate.Ranking -> navigator.toRanking(action.group)
            is HomeViewAction.RegisterWorkoutLog -> registerWorkoutLog(action.log)
            is HomeViewAction.ShowRegisterWorkoutDialog -> showRegisterWorkoutDialog()
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
                    _state.update { it.copy(message = getString(Res.string.workout_saved_successfully)) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
            showRegisterWorkoutDialog()
            setLoading(false)
        }
    }

    private fun cleanMessages() {
        _state.update { it.copy(error = null, message = null) }
    }
}