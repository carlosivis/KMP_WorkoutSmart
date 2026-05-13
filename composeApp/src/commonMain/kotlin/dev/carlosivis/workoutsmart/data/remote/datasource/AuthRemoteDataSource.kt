package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.workoutsmart.shared.UserResponse
import dev.gitlive.firebase.auth.FirebaseUser

interface AuthRemoteDataSource {
    suspend fun loginWithBackend(firebaseUser: FirebaseUser): Result<UserResponse>
}