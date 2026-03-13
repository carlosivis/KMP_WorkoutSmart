package dev.carlosivis.workoutsmart.screens.profile

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.domain.repository.AuthRepository
import dev.carlosivis.workoutsmart.domain.usecase.GetUserUseCase
import dev.carlosivis.workoutsmart.domain.usecase.LoginGoogleUseCase
import dev.carlosivis.workoutsmart.domain.usecase.LogoutUseCase
import dev.carlosivis.workoutsmart.models.UserResponse
import dev.carlosivis.workoutsmart.navigation.navigator.ProfileNavigator
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val authRepository = mock<AuthRepository>()
    private val loginGoogleUseCase = LoginGoogleUseCase(authRepository, testDispatcher)
    private val getUserUseCase = GetUserUseCase(authRepository, testDispatcher)
    private val logoutUseCase = LogoutUseCase(authRepository, testDispatcher)
    private val navigator = mock<ProfileNavigator>(MockMode.autofill)

    private val testUser = UserResponse(1, "uid", "test@test.com", "Test User", 100L, null)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        everySuspend { authRepository.getUser() } returns Result.success(null)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then should fetch user profile`() = runTest {
        ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend(VerifyMode.exactly(1)) { authRepository.getUser() }
    }

    @Test
    fun `when init with logged user then should update state with user`() = runTest {
        everySuspend { authRepository.getUser() } returns Result.success(testUser)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(testUser, awaitItem().user)
        }
    }

    @Test
    fun `when dispatch GoogleLogin then should update state with user`() = runTest {
        everySuspend { authRepository.loginWithGoogle() } returns Result.success(testUser)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.GoogleLogin)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testUser, state.user)
            assertEquals(false, state.isLoading)
        }
        verifySuspend(VerifyMode.exactly(1)) { authRepository.loginWithGoogle() }
    }

    @Test
    fun `when dispatch GoogleLogin fails then should set error state`() = runTest {
        val error = Exception("Login failed")
        everySuspend { authRepository.loginWithGoogle() } returns Result.failure(error)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.GoogleLogin)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Login failed", state.error)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.user)
        }
    }

    @Test
    fun `when dispatch Logout then should clear user state`() = runTest {
        everySuspend { authRepository.getUser() } returns Result.success(testUser)
        everySuspend { authRepository.logout() } returns Result.success(Unit)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.Logout)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(null, awaitItem().user)
        }
        verifySuspend(VerifyMode.exactly(1)) { authRepository.logout() }
    }

    @Test
    fun `when dispatch Logout fails then should set error state`() = runTest {
        val error = Exception("Logout failed")
        everySuspend { authRepository.logout() } returns Result.failure(error)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.Logout)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals("Logout failed", awaitItem().error)
        }
    }

    @Test
    fun `when dispatch Navigate Back then should call navigator`() = runTest {
        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.Navigate.Back)

        verify { navigator.back() }
    }

    @Test
    fun `when dispatch Navigate Settings then should call navigator`() = runTest {
        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.Navigate.Settings)

        verify { navigator.toSettings() }
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val error = Exception("Login failed")
        everySuspend { authRepository.loginWithGoogle() } returns Result.failure(error)

        val viewModel =
            ProfileViewModel(loginGoogleUseCase, getUserUseCase, logoutUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(ProfileViewAction.GoogleLogin)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals("Login failed", awaitItem().error)
        }

        viewModel.dispatchAction(ProfileViewAction.CleanMessages)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.error)
            assertEquals(null, state.message)
        }
    }
}
