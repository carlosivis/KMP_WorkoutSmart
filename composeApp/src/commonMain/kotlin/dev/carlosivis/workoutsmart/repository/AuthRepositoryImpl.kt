package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.data.local.datasource.UserLocalDataSource
import dev.carlosivis.workoutsmart.data.remote.datasource.AuthRemoteDataSource
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.plataform.GoogleAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val googleAuth: GoogleAuthProvider,
    private val dataSource: AuthRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : AuthRepository {

    override suspend fun loginWithGoogle(): Result<UserResponse> {
        return try {
            val credential = googleAuth.getCredential()
                ?: return Result.failure(Exception("Login cancelado pelo usuário"))

            val authResult = auth.signInWithCredential(credential)
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Falha ao recuperar usuário Firebase"))

            val firebaseToken = firebaseUser.getIdToken(false)
                ?: return Result.failure(Exception("Falha ao obter token"))

            return dataSource.loginWithBackend(firebaseUser).onSuccess {
                userLocalDataSource.saveUserToken(firebaseToken)
            }

        } catch (e: Exception) {
            auth.signOut()
            e.printStackTrace()
            Result.failure(e)
        }
    }
}