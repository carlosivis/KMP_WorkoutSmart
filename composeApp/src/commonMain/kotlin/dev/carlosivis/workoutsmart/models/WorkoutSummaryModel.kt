package dev.carlosivis.workoutsmart.models

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSummaryModel(
    val id: Long ,
    val name: String ,
    val description: String ,
)