package dev.carlosivis.workoutsmart.models

import kotlinx.serialization.Serializable

@Serializable
data class HistoryModel(
    val id: Long,
    val date: String,
    val workoutName: String
)
