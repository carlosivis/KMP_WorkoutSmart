package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutUseCaseTest {

    private val repository = mock<AuthRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: LogoutUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = LogoutUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute then should return success and call repository logout`() = runTest {
        everySuspend { repository.logout() } returns Result.success(Unit)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        verifySuspend { repository.logout() }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Logout failed")
        everySuspend { repository.logout() } returns Result.failure(error)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.logout() }
    }

    @Test
    fun `when execute multiple times then should call repository each time`() = runTest {
        everySuspend { repository.logout() } returns Result.success(Unit)

        val result1 = useCase(Unit)
        val result2 = useCase(Unit)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(Unit, result1.getOrNull())
        assertEquals(Unit, result2.getOrNull())
    }

    @Test
    fun `when execute with network error then should return failure with proper message`() = runTest {
        val networkError = Exception("No internet connection")
        everySuspend { repository.logout() } returns Result.failure(networkError)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
        verifySuspend { repository.logout() }
    }

    @Test
    fun `when execute then should clear user session from repository`() = runTest {
        everySuspend { repository.logout() } returns Result.success(Unit)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        verifySuspend { repository.logout() }
    }
}

