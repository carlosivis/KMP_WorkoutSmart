package dev.carlosivis.workoutsmart.data.remote.datasource

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.features.workoutlog.WorkoutType
import dev.carlosivis.workoutsmart.core.NetworkWrapper
import dev.carlosivis.workoutsmart.data.remote.service.SocialService
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SocialRemoteDataSourceImplTest {

    private fun jsonHeaders() = headersOf(HttpHeaders.ContentType, "application/json")

    private val testJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private fun buildDataSource(handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): SocialRemoteDataSourceImpl {
        val mockEngine = MockEngine { request -> handler(request) }
        val httpClient = HttpClient(mockEngine) {
            expectSuccess = false

            install(ContentNegotiation) {
                json(testJson)
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
        return SocialRemoteDataSourceImpl(SocialService(httpClient))
    }

    private val validGroupJson = """{"id":1,"name":"Group 1","inviteCode":"CODE123","userScore":100,"userPosition":1}"""

    @Test
    fun `when joinGroup returns 200 then should return success`() = runTest {
        val dataSource = buildDataSource {
            respond(content = validGroupJson, status = HttpStatusCode.OK, headers = jsonHeaders())
        }

        val result = dataSource.joinGroup(JoinGroupRequest("CODE123"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `when createGroup returns 201 then should return success`() = runTest {
        val dataSource = buildDataSource {
            respond(content = validGroupJson, status = HttpStatusCode.Created, headers = jsonHeaders())
        }

        val result = dataSource.createGroup(CreateGroupRequest("My Group", "description"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `when registerWorkoutLog returns 200 then should return success`() = runTest {
        val dataSource = buildDataSource {
            respond(content = "{}", status = HttpStatusCode.OK, headers = jsonHeaders())
        }

        val result = dataSource.registerWorkoutLog(
            WorkoutLogRequest(type = WorkoutType.GYM, durationInSeconds = 3600L, description = "Chest Day")
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `when registerWorkoutLog returns 500 then should return failure with ServerException`() = runTest {
        val dataSource = buildDataSource {
            respond(content = """{"error":"Server Error"}""", status = HttpStatusCode.InternalServerError, headers = jsonHeaders())
        }

        val result = dataSource.registerWorkoutLog(
            WorkoutLogRequest(type = WorkoutType.GYM, durationInSeconds = 6000, description = "Leg Day")
        )

        assertTrue(result.isFailure)
        assertIs<NetworkWrapper.ServerException>(result.exceptionOrNull())
    }

    @Test
    fun `when createGroup returns 400 then should return failure with BadRequestException`() = runTest {
        val dataSource = buildDataSource {
            respond(content = """{"error":"Bad Request"}""", status = HttpStatusCode.BadRequest, headers = jsonHeaders())
        }

        val result = dataSource.createGroup(CreateGroupRequest("My Group", "description"))

        assertTrue(result.isFailure)
        assertIs<NetworkWrapper.BadRequestException>(result.exceptionOrNull())
    }

    @Test
    fun `when createGroup returns 409 then should return failure with UnknownCodeException`() = runTest {
        val dataSource = buildDataSource {
            respond(content = """{"error":"Conflict"}""", status = HttpStatusCode.Conflict, headers = jsonHeaders())
        }

        val result = dataSource.createGroup(CreateGroupRequest("My Group", "description"))

        assertTrue(result.isFailure)
        assertIs<NetworkWrapper.UnknownCodeException>(result.exceptionOrNull())
    }

    @Test
    fun `when joinGroup returns 403 then should return failure with ForbiddenException`() = runTest {
        val dataSource = buildDataSource {
            respond(content = """{"error":"Forbidden"}""", status = HttpStatusCode.Forbidden, headers = jsonHeaders())
        }

        val result = dataSource.joinGroup(JoinGroupRequest("FORBIDDEN"))

        assertTrue(result.isFailure)
        assertIs<NetworkWrapper.ForbiddenException>(result.exceptionOrNull())
    }

    @Test
    fun `when joinGroup returns 404 then should return failure with NotFoundException`() = runTest {
        val dataSource = buildDataSource {
            respond(content = """{"error":"Not Found"}""", status = HttpStatusCode.NotFound, headers = jsonHeaders())
        }

        val result = dataSource.joinGroup(JoinGroupRequest("NOTFOUND"))

        assertTrue(result.isFailure)
        assertIs<NetworkWrapper.NotFoundException>(result.exceptionOrNull())
    }
}