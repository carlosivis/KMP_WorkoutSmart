package dev.carlosivis.workoutsmart.models

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseModel(
    val id: Int,
    val name: String,
    val notes: String,
    val series: Int,
    val repetitions: Int,
    val videoUrl: String? = null,
    val imageUrl: String? = null
){
    companion object {
        fun empty() = ExerciseModel(
            id = 0,
            name = "",
            notes = "",
            series = 0,
            repetitions = 0,
            videoUrl = null,
            imageUrl = null
        )
    }
}
