package dev.carlosivis.workoutsmart.models

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseModel(
    val id: Int,
    val name: String,
    val notes: String,
    val series: Int,
    val repetitions: Int,
    val image: ByteArray?,
){
    companion object {
        fun empty() = ExerciseModel(
            id = 0,
            name = "",
            notes = "",
            series = 0,
            repetitions = 0,
            image = null
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExerciseModel

        if (id != other.id) return false
        if (series != other.series) return false
        if (repetitions != other.repetitions) return false
        if (name != other.name) return false
        if (notes != other.notes) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + series
        result = 31 * result + repetitions
        result = 31 * result + name.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}
