package dev.carlosivis.workoutsmart.data.local.datasource

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class HistoryLocalDataSourceImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var driver: SqlDriver
    private lateinit var dataSource: HistoryLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        driver = createTestDriver()
        dataSource = HistoryLocalDataSourceImpl(driver, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        driver.close()
    }


    @Test
    fun `when no history then getAllHistory should return empty list`() = runTest {
        val result = dataSource.getAllHistory().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when insertHistory then getAllHistory should return inserted entry`() = runTest {
        dataSource.insertHistory("Chest Day", 1609459200000L, 3600L)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllHistory().first()

        assertEquals(1, result.size)
        assertEquals("Chest Day", result[0].workoutName)
        assertEquals(1609459200000L, result[0].date)
        assertEquals(3600L, result[0].duration)
    }

    @Test
    fun `when insertHistory multiple entries then getAllHistory should return all`() = runTest {
        dataSource.insertHistory("Chest Day", 1609459200000L, 3600L)
        dataSource.insertHistory("Leg Day", 1609545600000L, 4200L)
        dataSource.insertHistory("Arm Day", 1609718400000L, 3000L)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllHistory().first()

        assertEquals(3, result.size)
    }

    @Test
    fun `when insertHistory then should persist all fields correctly`() = runTest {
        val workoutName = "Back Day"
        val date = 1609718400000L
        val duration = 5400L

        dataSource.insertHistory(workoutName, date, duration)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllHistory().first().first()

        assertEquals(workoutName, result.workoutName)
        assertEquals(date, result.date)
        assertEquals(duration, result.duration)
    }

    @Test
    fun `when insertHistory with same workout name then should create separate entries`() = runTest {
        dataSource.insertHistory("Chest Day", 1609459200000L, 3600L)
        dataSource.insertHistory("Chest Day", 1609545600000L, 3700L)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = dataSource.getAllHistory().first()

        assertEquals(2, result.size)
    }
}