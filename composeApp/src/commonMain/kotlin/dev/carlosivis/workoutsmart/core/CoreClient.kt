package dev.carlosivis.workoutsmart.core

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object CoreClient {
    private const val TIMEOUT_LIMIT = 15000L

    operator fun invoke(
        jsonConfig: Json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    ): HttpClient {
        return HttpClient {
            install(Logging) {
                level = LogLevel.INFO
            }
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_LIMIT
                connectTimeoutMillis = TIMEOUT_LIMIT
                socketTimeoutMillis = TIMEOUT_LIMIT
            }
        }

    }
}