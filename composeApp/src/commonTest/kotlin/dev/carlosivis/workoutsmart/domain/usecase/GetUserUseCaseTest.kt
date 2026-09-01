package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import dev.carlosivis.workoutsmart.models.UserResponse
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserUseCaseTest {

    private val repository = mock<AuthRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetUserUseCase

    private val testUser = UserResponse(
        id = 1,
        firebaseUid = "firebase123",
        email = "test@example.com",
        displayName = "Test User",
        points = 1000L,
        photoUrl = "https://example.com/photo.jpg"
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetUserUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute then should return success with user response`() = runTest {
        everySuspend { repository.getUser() } returns Result.success(testUser)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("Test User", user.displayName)
        assertEquals(1000L, user.points)
        verifySuspend { repository.getUser() }
    }

    @Test
    fun `when execute and user is not authenticated then should return success with null`() = runTest {
        everySuspend { repository.getUser() } returns Result.success(null)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
        verifySuspend { repository.getUser() }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Authentication error")
        everySuspend { repository.getUser() } returns Result.failure(error)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.getUser() }
    }

    @Test
    fun `when execute multiple times then should return consistent user data`() = runTest {
        everySuspend { repository.getUser() } returns Result.success(testUser)

        val result1 = useCase(Unit)
        val result2 = useCase(Unit)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())
        assertEquals("Test User", result1.getOrNull()?.displayName)
        assertEquals("Test User", result2.getOrNull()?.displayName)
    }

    @Test
    fun `when execute with different user data then should return correct user information`() = runTest {
        val differentUser = UserResponse(
            id = 2,
            firebaseUid = "firebase456",
            email = "another@example.com",
            displayName = "Another User",
            points = 500L,
            photoUrl = "https://example.com/photo2.jpg"
        )

        everySuspend { repository.getUser() } returns Result.success(differentUser)

        val result = useCase(Unit)

        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("another@example.com", user.email)
        assertEquals("Another User", user.displayName)
        assertEquals(500L, user.points)
    }
}

