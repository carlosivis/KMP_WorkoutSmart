package dev.carlosivis.features.workoutlog

import kotlinx.serialization.Serializable


enum class WorkoutType(val displayName: String) {
    GYM("Academia"),
    RUNNING("Corrida"),
    CYCLING("Ciclismo"),
    SPORTS("Esporte"),
    CROSSFIT("Crossfit"),
    OTHER("Outro")
}

@Serializable
data class WorkoutLogRequest(
    val type: WorkoutType,
    val description: String? = null,
    val durationInSeconds: Long? = null,
)