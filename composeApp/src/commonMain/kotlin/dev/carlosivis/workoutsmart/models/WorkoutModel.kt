package dev.carlosivis.workoutsmart.models

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutModel(
    val id: Long ,
    val name: String ,
    val description: String ,
    val exercises: List<ExerciseModel>
) {
    companion object {
        fun empty() = WorkoutModel(
            id = 0,
            name = "",
            description = "",
            exercises = emptyList()
        )
    }
}
