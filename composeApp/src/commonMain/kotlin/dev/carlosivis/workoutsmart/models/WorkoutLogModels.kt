package dev.carlosivis.features.workoutlog

import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.workout_type_crossfit
import dev.carlosivis.workoutsmart.composeResources.workout_type_cycling
import dev.carlosivis.workoutsmart.composeResources.workout_type_gym
import dev.carlosivis.workoutsmart.composeResources.workout_type_other
import dev.carlosivis.workoutsmart.composeResources.workout_type_running
import dev.carlosivis.workoutsmart.composeResources.workout_type_sports
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource


enum class WorkoutType(val displayName: StringResource) {
    GYM(Res.string.workout_type_gym),
    RUNNING(Res.string.workout_type_running),
    CYCLING(Res.string.workout_type_cycling),
    SPORTS(Res.string.workout_type_sports),
    CROSSFIT(Res.string.workout_type_crossfit),
    OTHER(Res.string.workout_type_other)
}

@Serializable
data class WorkoutLogRequest(
    val type: WorkoutType,
    val description: String? = null,
    val durationInSeconds: Long? = null,
)