package dev.carlosivis.workoutsmart.core

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException

object NetworkWrapper {

    suspend inline fun <reified T> safeCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): T {
        return try {
            val response = apiCall()

            if (response.status.isSuccess()) {
                response.body()
            } else {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    response.status.description
                }

                throw mapHttpException(
                    code = response.status.value,
                    message = errorBody
                )
            }

        } catch (e: Exception) {
            throw mapThrowable(e)
        }
    }

    fun mapThrowable(throwable: Throwable): AppNetworkException {
        return when (throwable) {
            is AppNetworkException -> throwable
            is ResponseException -> mapHttpException(
                code = throwable.response.status.value,
                message = throwable.message)
            is TimeoutCancellationException -> TimeOutException("Request timed out")
            is IOException -> NoInternetException("No internet connection")
            else -> UnknownNetworkException(throwable.message ?: "Unknown network error")
        }
    }

    fun mapHttpException(code: Int, message: String?): AppNetworkException {
        val safeMessage = message?.takeIf { it.isNotBlank() }
        return when (code) {
            400 -> BadRequestException(safeMessage ?: "Bad request")
            401 -> UnauthorizedException(safeMessage ?: "Unauthorized")
            403 -> ForbiddenException(safeMessage ?: "Forbidden")
            404 -> NotFoundException(safeMessage ?: "Not found")
            408 -> TimeOutException(safeMessage ?: "Request timed out")
            in 500..599 -> ServerException(safeMessage ?: "Server error")
            else -> UnknownCodeException(safeMessage ?: "Unexpected status code: $code")
        }
    }

    sealed class AppNetworkException(
        override val message: String
    ) : Exception(message)

    class BadRequestException(message: String) : AppNetworkException(message)
    class UnauthorizedException(message: String) : AppNetworkException(message)
    class ForbiddenException(message: String) : AppNetworkException(message)
    class NotFoundException(message: String) : AppNetworkException(message)
    class TimeOutException(message: String) : AppNetworkException(message)
    class ServerException(message: String) : AppNetworkException(message)
    class UnknownCodeException(message: String) : AppNetworkException(message)
    class NoInternetException(message: String) : AppNetworkException(message)
    class UnknownNetworkException(message: String) : AppNetworkException(message)
}
