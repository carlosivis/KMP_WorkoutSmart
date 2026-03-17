package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.MapSettings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsLocalDataSourceImplTest {

    private lateinit var settings: MapSettings
    private lateinit var dataSource: SettingsLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        settings = MapSettings()
        dataSource = SettingsLocalDataSourceImpl(settings)
    }

    @AfterTest
    fun tearDown() {
        settings.clear()
    }

    @Test
    fun `when getThemeOrdinal without saving then should return default value -1`() {
        val themeOrdinal = dataSource.getThemeOrdinal()

        assertEquals(-1, themeOrdinal)
    }

    @Test
    fun `when saveThemeOrdinal then should store and retrieve theme ordinal`() {
        dataSource.saveThemeOrdinal(2)

        val retrievedOrdinal = dataSource.getThemeOrdinal()
        assertEquals(2, retrievedOrdinal)
    }

    @Test
    fun `when saveThemeOrdinal multiple times then should override previous value`() {
        dataSource.saveThemeOrdinal(0)
        assertEquals(0, dataSource.getThemeOrdinal())

        dataSource.saveThemeOrdinal(1)
        assertEquals(1, dataSource.getThemeOrdinal())

        dataSource.saveThemeOrdinal(3)
        assertEquals(3, dataSource.getThemeOrdinal())
    }

    @Test
    fun `when getRestTime without saving then should return default value 60`() {
        val restTime = dataSource.getRestTime()

        assertEquals(60, restTime)
    }

    @Test
    fun `when saveRestTime then should store and retrieve rest time`() {
        dataSource.saveRestTime(90)

        val retrievedRestTime = dataSource.getRestTime()
        assertEquals(90, retrievedRestTime)
    }

    @Test
    fun `when saveRestTime with various values then should store correctly`() {
        listOf(30, 60, 120, 180, 300).forEach { restTime ->
            dataSource.saveRestTime(restTime)
            assertEquals(restTime, dataSource.getRestTime())
        }
    }

    @Test
    fun `when isScreenOnEnabled without saving then should return default value false`() {
        val isEnabled = dataSource.isScreenOnEnabled()

        assertFalse(isEnabled)
    }

    @Test
    fun `when saveScreenOn true then should retrieve as true`() {
        dataSource.saveScreenOn(true)

        assertTrue(dataSource.isScreenOnEnabled())
    }

    @Test
    fun `when saveScreenOn false then should retrieve as false`() {
        dataSource.saveScreenOn(false)

        assertFalse(dataSource.isScreenOnEnabled())
    }

    @Test
    fun `when isVibrationEnabled without saving then should return default value true`() {
        val isEnabled = dataSource.isVibrationEnabled()

        assertTrue(isEnabled)
    }

    @Test
    fun `when saveVibration true then should retrieve as true`() {
        dataSource.saveVibration(true)

        assertTrue(dataSource.isVibrationEnabled())
    }

    @Test
    fun `when saveVibration false then should retrieve as false`() {
        dataSource.saveVibration(false)

        assertFalse(dataSource.isVibrationEnabled())
    }

    @Test
    fun `when save all settings then should retrieve all values correctly`() {
        dataSource.saveThemeOrdinal(1)
        dataSource.saveRestTime(120)
        dataSource.saveScreenOn(true)
        dataSource.saveVibration(false)

        assertEquals(1, dataSource.getThemeOrdinal())
        assertEquals(120, dataSource.getRestTime())
        assertTrue(dataSource.isScreenOnEnabled())
        assertFalse(dataSource.isVibrationEnabled())
    }

    @Test
    fun `when save settings independently then should not affect each other`() {
        dataSource.saveThemeOrdinal(2)

        assertEquals(2, dataSource.getThemeOrdinal())
        assertEquals(60, dataSource.getRestTime())
        assertFalse(dataSource.isScreenOnEnabled())
        assertTrue(dataSource.isVibrationEnabled())
    }

    @Test
    fun `when saveThemeOrdinal with edge values then should store correctly`() {
        dataSource.saveThemeOrdinal(0)
        assertEquals(0, dataSource.getThemeOrdinal())

        dataSource.saveThemeOrdinal(Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, dataSource.getThemeOrdinal())

        dataSource.saveThemeOrdinal(Int.MIN_VALUE)
        assertEquals(Int.MIN_VALUE, dataSource.getThemeOrdinal())
    }

    @Test
    fun `when saveRestTime with edge values then should store correctly`() {
        dataSource.saveRestTime(0)
        assertEquals(0, dataSource.getRestTime())

        dataSource.saveRestTime(Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, dataSource.getRestTime())
    }
}
