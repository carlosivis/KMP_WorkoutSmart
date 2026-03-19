package dev.carlosivis.workoutsmart.data.repository

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.data.local.datasource.SettingsLocalDataSource
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.utils.ThemeMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {

    private val dataSource = mock<SettingsLocalDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SettingsRepositoryImpl

    private val defaultSettings = SettingsModel(
        themeMode = ThemeMode.SYSTEM,
        defaultRestSeconds = 60,
        keepScreenOn = false,
        vibrationEnabled = true
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { dataSource.getThemeOrdinal() } returns ThemeMode.SYSTEM.ordinal
        every { dataSource.getRestTime() } returns 60
        every { dataSource.isScreenOnEnabled() } returns false
        every { dataSource.isVibrationEnabled() } returns true
        repository = SettingsRepositoryImpl(dataSource)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getSettings then should emit current settings from flow`() = runTest {
        repository.getSettings().test {
            val settings = awaitItem()
            assertEquals(ThemeMode.SYSTEM, settings.themeMode)
            assertEquals(60, settings.defaultRestSeconds)
            assertEquals(false, settings.keepScreenOn)
            assertEquals(true, settings.vibrationEnabled)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when saveSettings then should update local data source and emit new settings`() = runTest {
        val newSettings = SettingsModel(
            themeMode = ThemeMode.DARK,
            defaultRestSeconds = 90,
            keepScreenOn = true,
            vibrationEnabled = false
        )
        everySuspend { dataSource.saveThemeOrdinal(ThemeMode.DARK.ordinal) } returns Unit
        everySuspend { dataSource.saveRestTime(90) } returns Unit
        everySuspend { dataSource.saveScreenOn(true) } returns Unit
        everySuspend { dataSource.saveVibration(false) } returns Unit

        repository.saveSettings(newSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.getSettings().test {
            val settings = awaitItem()
            assertEquals(ThemeMode.DARK, settings.themeMode)
            assertEquals(90, settings.defaultRestSeconds)
            assertEquals(true, settings.keepScreenOn)
            assertEquals(false, settings.vibrationEnabled)
            cancelAndIgnoreRemainingEvents()
        }

        verifySuspend { dataSource.saveThemeOrdinal(ThemeMode.DARK.ordinal) }
        verifySuspend { dataSource.saveRestTime(90) }
        verifySuspend { dataSource.saveScreenOn(true) }
        verifySuspend { dataSource.saveVibration(false) }
    }

    @Test
    fun `when saveSettings with light theme then should save light theme ordinal`() = runTest {
        val newSettings = SettingsModel(
            themeMode = ThemeMode.LIGHT,
            defaultRestSeconds = 120,
            keepScreenOn = false,
            vibrationEnabled = true
        )
        everySuspend { dataSource.saveThemeOrdinal(ThemeMode.LIGHT.ordinal) } returns Unit
        everySuspend { dataSource.saveRestTime(120) } returns Unit
        everySuspend { dataSource.saveScreenOn(false) } returns Unit
        everySuspend { dataSource.saveVibration(true) } returns Unit

        repository.saveSettings(newSettings)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { dataSource.saveThemeOrdinal(ThemeMode.LIGHT.ordinal) }
    }

    @Test
    fun `when saveSettings multiple times then should update settings each time`() = runTest {
        val settings1 = SettingsModel(ThemeMode.DARK, 60, false, true)
        val settings2 = SettingsModel(ThemeMode.LIGHT, 90, true, false)

        everySuspend { dataSource.saveThemeOrdinal(any()) } returns Unit
        everySuspend { dataSource.saveRestTime(any()) } returns Unit
        everySuspend { dataSource.saveScreenOn(any()) } returns Unit
        everySuspend { dataSource.saveVibration(any()) } returns Unit

        repository.saveSettings(settings1)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.saveSettings(settings2)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.getSettings().test {
            val finalSettings = awaitItem()
            assertEquals(ThemeMode.LIGHT, finalSettings.themeMode)
            assertEquals(90, finalSettings.defaultRestSeconds)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when saveSettings with different rest times then should save correct durations`() = runTest {
        val settings30s = SettingsModel(ThemeMode.SYSTEM, 30, false, true)
        val settings120s = SettingsModel(ThemeMode.SYSTEM, 120, false, true)

        everySuspend { dataSource.saveThemeOrdinal(any()) } returns Unit
        everySuspend { dataSource.saveRestTime(30) } returns Unit
        everySuspend { dataSource.saveScreenOn(any()) } returns Unit
        everySuspend { dataSource.saveVibration(any()) } returns Unit

        repository.saveSettings(settings30s)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { dataSource.saveRestTime(30) }

        everySuspend { dataSource.saveRestTime(120) } returns Unit
        repository.saveSettings(settings120s)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { dataSource.saveRestTime(120) }
    }

    @Test
    fun `when getSettings with invalid theme ordinal then should default to SYSTEM`() = runTest {
        every { dataSource.getThemeOrdinal() } returns -1
        val newRepository = SettingsRepositoryImpl(dataSource)

        newRepository.getSettings().test {
            val settings = awaitItem()
            assertEquals(ThemeMode.SYSTEM, settings.themeMode)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

