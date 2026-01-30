package dev.carlosivis.workoutsmart.data.remote.service

import dev.carlosivis.workoutsmart.models.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class AuthService(private val client: HttpClient) {

    suspend fun login(token: String, request: LoginRequest): HttpResponse {
        return client.post("auth/login") {
            header("Authorization", "Bearer $token")
            setBody(request)
        }
    }
}