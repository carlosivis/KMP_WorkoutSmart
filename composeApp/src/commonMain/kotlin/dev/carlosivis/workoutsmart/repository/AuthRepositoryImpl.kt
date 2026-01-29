package dev.carlosivis.workoutsmart.repository

import dev.carlosivis.workoutsmart.models.LoginRequest
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.plataform.GoogleAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val googleAuth: GoogleAuthProvider,
    private val client: HttpClient
) : AuthRepository {

    override suspend fun loginWithGoogle(): Result<UserResponse> {
        return try {
            val credential = googleAuth.getCredential()
                ?: return Result.failure(Exception("Login cancelado pelo usuário"))

            val authResult = auth.signInWithCredential(credential)
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Falha ao recuperar usuário Firebase"))


            val idToken = firebaseUser.getIdToken(false)
                ?: return Result.failure(Exception("Não foi possível obter o token de autenticação"))

            //TODO: create service layer
            val backendResponse = client.post("auth/login") { // Ajuste a rota conforme seu backend
                header("Authorization", "Bearer $idToken")

                setBody(
                    LoginRequest(
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName,
                        //photoUrl = firebaseUser.photoURL
                    )
                )
            }.body<UserResponse>()

            Result.success(backendResponse)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}