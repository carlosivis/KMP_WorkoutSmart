package dev.carlosivis.workoutsmart.core

import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.network_error_bad_request
import dev.carlosivis.workoutsmart.composeResources.network_error_forbidden
import dev.carlosivis.workoutsmart.composeResources.network_error_no_internet
import dev.carlosivis.workoutsmart.composeResources.network_error_not_found
import dev.carlosivis.workoutsmart.composeResources.network_error_server
import dev.carlosivis.workoutsmart.composeResources.network_error_timeout
import dev.carlosivis.workoutsmart.composeResources.network_error_unauthorized
import dev.carlosivis.workoutsmart.composeResources.network_error_unknown
import dev.carlosivis.workoutsmart.composeResources.network_error_unknown_code
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import org.jetbrains.compose.resources.getString

object NetworkWrapper {

    suspend inline fun <reified T> safeCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): T {
        return try {
            val response = apiCall()

            if (response.status.isSuccess()) {
                response.body()
            } else {
                val errorBody = runCatching {
                    response.body<String>()
                }.getOrNull()

                throw mapHttpException(
                    code = response.status.value,
                    message = errorBody
                )
            }

        } catch (e: Exception) {
            throw mapThrowable(e)
        }
    }

    suspend fun mapThrowable(throwable: Throwable): AppNetworkException {
        return when (throwable) {
            is AppNetworkException -> throwable
            is TimeoutCancellationException -> TimeOutException(getString(Res.string.network_error_timeout))
            is IOException -> NoInternetException(getString(Res.string.network_error_no_internet))
            else -> UnknownNetworkException(getString(Res.string.network_error_unknown))
        }
    }

    suspend fun mapHttpException(
        code: Int,
        message: String?
    ): AppNetworkException {
        val safeMessage = message?.takeIf { it.isNotBlank() }

        return when (code) {
            400 -> BadRequestException(safeMessage ?: getString(Res.string.network_error_bad_request))
            401 -> UnauthorizedException(safeMessage ?: getString(Res.string.network_error_unauthorized))
            403 -> ForbiddenException(safeMessage ?: getString(Res.string.network_error_forbidden))
            404 -> NotFoundException(safeMessage ?: getString(Res.string.network_error_not_found))
            408 -> TimeOutException(getString(Res.string.network_error_timeout))
            in 500..599 -> ServerException(safeMessage ?: getString(Res.string.network_error_server))
            else -> UnknownCodeException(getString(Res.string.network_error_unknown_code, code))
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
