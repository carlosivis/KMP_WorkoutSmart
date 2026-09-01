package dev.carlosivis.workoutsmart.screens.settings

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.domain.repository.SettingsRepository
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.utils.ThemeMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val repository = mock<SettingsRepository>()
    private var navigateBackCalled = false
    private val onNavigateBack: () -> Unit = { navigateBackCalled = true }
    
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then should get settings from repository`() = runTest {
        val settings = SettingsModel.default()
        every { repository.getSettings() } returns flowOf(settings)

        val viewModel = SettingsViewModel(repository, onNavigateBack)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(settings, awaitItem().settings)
        }

        verify { repository.getSettings() }
    }

    @Test
    fun `when dispatch UpdateThemeMode action then should update state and save to repository`() = runTest {
        val initialSettings = SettingsModel.default()
        val settingsFlow = MutableSharedFlow<SettingsModel>(replay = 1)
        settingsFlow.emit(initialSettings)

        every { repository.getSettings() } returns settingsFlow
        everySuspend { repository.saveSettings(any()) } returns Unit

        val viewModel = SettingsViewModel(repository, onNavigateBack)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(SettingsViewAction.UpdateThemeMode(ThemeMode.DARK))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(ThemeMode.DARK, awaitItem().settings.themeMode)
        }

        verifySuspend { repository.saveSettings(initialSettings.copy(themeMode = ThemeMode.DARK)) }
    }

    @Test
    fun `when dispatch UpdateDefaultRestTime action then should update state and save to repository`() = runTest {
        val initialSettings = SettingsModel.default()
        every { repository.getSettings() } returns flowOf(initialSettings)
        everySuspend { repository.saveSettings(any()) } returns Unit

        val viewModel = SettingsViewModel(repository, onNavigateBack)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(SettingsViewAction.UpdateDefaultRestTime(30))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(30, awaitItem().settings.defaultRestSeconds)
        }

        verifySuspend { repository.saveSettings(initialSettings.copy(defaultRestSeconds = 30)) }
    }

    @Test
    fun `when dispatch UpdateKeepScreenOn action then should update state and save to repository`() = runTest {
        val initialSettings = SettingsModel.default()
        every { repository.getSettings() } returns flowOf(initialSettings)
        everySuspend { repository.saveSettings(any()) } returns Unit

        val viewModel = SettingsViewModel(repository, onNavigateBack)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(SettingsViewAction.UpdateKeepScreenOn(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(true, awaitItem().settings.keepScreenOn)
        }

        verifySuspend { repository.saveSettings(initialSettings.copy(keepScreenOn = true)) }
    }

    @Test
    fun `when dispatch UpdateVibrationEnabled action then should update state and save to repository`() = runTest {
        val initialSettings = SettingsModel.default()
        every { repository.getSettings() } returns flowOf(initialSettings)
        everySuspend { repository.saveSettings(any()) } returns Unit

        val viewModel = SettingsViewModel(repository, onNavigateBack)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(SettingsViewAction.UpdateVibrationEnabled(false))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertEquals(false, awaitItem().settings.vibrationEnabled)
        }

        verifySuspend { repository.saveSettings(initialSettings.copy(vibrationEnabled = false)) }
    }

    @Test
    fun `when dispatch NavigateBack action then should call onNavigateBack`() = runTest {
        every { repository.getSettings() } returns flowOf(SettingsModel.default())
        val viewModel = SettingsViewModel(repository, onNavigateBack)

        viewModel.dispatchAction(SettingsViewAction.NavigateBack)

        assertEquals(true, navigateBackCalled)
    }
}
