package dev.carlosivis.workoutsmart.core


import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

object NetworkWrapper {

    suspend inline fun <reified T> safeCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): T {
        try {
            val response = apiCall()

            if (response.status.isSuccess()) {
                return response.body<T>()
            } else {
                throw handleException(response.status.value)
            }
        } catch (e: Exception) {
            throw if (e is AppNetworkException) e else UnknownNetworkException(e)
        }
    }

    fun handleException(code: Int): Exception {
        return when (code) {
            400 -> BadRequestException()
            401 -> UnauthorizedException()
            404 -> NotFoundException()
            408 -> TimeOutException()
            in 500..599 -> ServerException()
            else -> UnknownCodeException()
        }
    }

    open class AppNetworkException(message: String? = null) : Exception(message)
    class BadRequestException : AppNetworkException()
    class UnauthorizedException : AppNetworkException()
    class NotFoundException : AppNetworkException()
    class TimeOutException : AppNetworkException()
    class ServerException : AppNetworkException()
    class UnknownCodeException : AppNetworkException()
    class UnknownNetworkException(cause: Throwable) : AppNetworkException(cause.message)
}

