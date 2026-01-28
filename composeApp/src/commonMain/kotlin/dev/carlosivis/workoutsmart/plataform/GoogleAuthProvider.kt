package dev.carlosivis.workoutsmart.plataform

interface GoogleAuthProvider {
    suspend fun signIn(): GoogleAuthResult
}

data class GoogleAuthResult(
    val idToken: String,
)