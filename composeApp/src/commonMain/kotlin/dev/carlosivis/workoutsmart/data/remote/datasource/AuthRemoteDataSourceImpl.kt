package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.workoutsmart.core.AppNetworkException
import dev.carlosivis.workoutsmart.core.NetworkWrapper
import dev.carlosivis.workoutsmart.data.remote.service.AuthService
import dev.carlosivis.workoutsmart.models.LoginRequest
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.gitlive.firebase.auth.FirebaseUser

class AuthRemoteDataSourceImpl(
    private val service: AuthService
) : AuthRemoteDataSource {

    override suspend fun loginWithBackend(firebaseUser: FirebaseUser): Result<UserResponse> {
        return try {
            val request = LoginRequest(
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoURL
            )
            val token = firebaseUser.getIdToken(false)
                ?: return Result.failure(Exception("Não foi possível obter o token de autenticação"))

            val response = NetworkWrapper.safeCall<UserResponse> {
                service.login(token,request)
            }

            Result.success(response)

        } catch (e: AppNetworkException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}