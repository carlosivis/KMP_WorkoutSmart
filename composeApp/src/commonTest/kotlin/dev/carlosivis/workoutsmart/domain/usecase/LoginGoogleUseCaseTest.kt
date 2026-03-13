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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginGoogleUseCaseTest {

    private val repository = mock<AuthRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: LoginGoogleUseCase

    private val testUser = UserResponse(
        id = 1,
        firebaseUid = "firebase123",
        email = "user@gmail.com",
        displayName = "Google User",
        points = 0L,
        photoUrl = "https://example.com/photo.jpg"
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = LoginGoogleUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute then should return success with user response from Google login`() = runTest {
        everySuspend { repository.loginWithGoogle() } returns Result.success(testUser)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("user@gmail.com", user?.email)
        assertEquals("Google User", user?.displayName)
        assertEquals(0L, user?.points)
        verifySuspend { repository.loginWithGoogle() }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Google login failed")
        everySuspend { repository.loginWithGoogle() } returns Result.failure(error)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.loginWithGoogle() }
    }

    @Test
    fun `when execute and user is already logged in then should return existing user data`() = runTest {
        val existingUser = UserResponse(
            id = 2,
            firebaseUid = "firebase456",
            email = "existing@gmail.com",
            displayName = "Existing User",
            points = 500L,
            photoUrl = "https://example.com/existing.jpg"
        )
        everySuspend { repository.loginWithGoogle() } returns Result.success(existingUser)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("existing@gmail.com", user?.email)
        assertEquals(500L, user?.points)
    }

    @Test
    fun `when execute multiple times then should call repository each time`() = runTest {
        everySuspend { repository.loginWithGoogle() } returns Result.success(testUser)

        val result1 = useCase(Unit)
        val result2 = useCase(Unit)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())
    }

    @Test
    fun `when execute with network error then should return failure with proper message`() = runTest {
        val networkError = Exception("No internet connection")
        everySuspend { repository.loginWithGoogle() } returns Result.failure(networkError)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
    }
}

