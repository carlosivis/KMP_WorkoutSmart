package dev.carlosivis.workoutsmart.core

import dev.gitlive.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType

object KtorClient {

    operator fun invoke(
        baseUrl: String,
        firebaseAuth: FirebaseAuth? = null
    ): HttpClient {
        val client = CoreClient()

        return client.config {
            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }

            if (firebaseAuth != null) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            val user = firebaseAuth.currentUser
                            val token = user?.getIdToken(forceRefresh = false)
                            token?.let { BearerTokens(it, "") }
                        }
                        refreshTokens {
                            val user = firebaseAuth.currentUser
                            val token = user?.getIdToken(forceRefresh = false)
                            token?.let { BearerTokens(it, "") }
                        }
                    }
                }
            }
        }
    }
}
