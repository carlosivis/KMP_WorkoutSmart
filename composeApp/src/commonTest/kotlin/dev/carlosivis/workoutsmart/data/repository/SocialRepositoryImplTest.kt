package dev.carlosivis.workoutsmart.data.repository

import dev.carlosivis.features.workoutlog.WorkoutLogRequest
import dev.carlosivis.features.workoutlog.WorkoutType
import dev.carlosivis.workoutsmart.data.remote.datasource.SocialRemoteDataSource
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.models.RankingMember
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
class SocialRepositoryImplTest {

    private val remoteDataSource = mock<SocialRemoteDataSource>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SocialRepositoryImpl

    private val testGroups = listOf(
        GroupResponse(1, "Group 1", "CODE1", 100L, 1),
        GroupResponse(2, "Group 2", "CODE2", 50L, 2)
    )

    private val testGroup = GroupResponse(3, "New Group", "CODE3", 0L, 1)

    private val testRankingMembers = listOf(
        RankingMember(1, "Alice", "https://example.com/alice.jpg", 1000L),
        RankingMember(2, "Bob", "https://example.com/bob.jpg", 900L)
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = SocialRepositoryImpl(remoteDataSource)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getGroups then should return success with groups list`() = runTest {
        everySuspend { remoteDataSource.getGroups() } returns Result.success(testGroups)

        val result = repository.getGroups()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Group 1", result.getOrNull()?.get(0)?.name)
        verifySuspend { remoteDataSource.getGroups() }
    }

    @Test
    fun `when getGroups and remote returns empty list then should return success with empty list`() = runTest {
        everySuspend { remoteDataSource.getGroups() } returns Result.success(emptyList())

        val result = repository.getGroups()

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        verifySuspend { remoteDataSource.getGroups() }
    }

    @Test
    fun `when getGroups and remote throws exception then should return failure`() = runTest {
        val error = Exception("Network error")
        everySuspend { remoteDataSource.getGroups() } returns Result.failure(error)

        val result = repository.getGroups()

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { remoteDataSource.getGroups() }
    }

    @Test
    fun `when createGroup with valid request then should return success with group response`() = runTest {
        val request = CreateGroupRequest("New Group", "Description")
        everySuspend { remoteDataSource.createGroup(request) } returns Result.success(testGroup)

        val result = repository.createGroup(request)

        assertTrue(result.isSuccess)
        assertEquals("New Group", result.getOrNull()?.name)
        verifySuspend { remoteDataSource.createGroup(request) }
    }

    @Test
    fun `when createGroup and remote throws exception then should return failure`() = runTest {
        val request = CreateGroupRequest("Failed Group", null)
        val error = Exception("Create failed")
        everySuspend { remoteDataSource.createGroup(request) } returns Result.failure(error)

        val result = repository.createGroup(request)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { remoteDataSource.createGroup(request) }
    }

    @Test
    fun `when joinGroup with valid invite code then should return success with group response`() = runTest {
        val request = JoinGroupRequest("INVITECODE123")
        everySuspend { remoteDataSource.joinGroup(request) } returns Result.success(testGroup)

        val result = repository.joinGroup(request)

        assertTrue(result.isSuccess)
        assertEquals("New Group", result.getOrNull()?.name)
        verifySuspend { remoteDataSource.joinGroup(request) }
    }

    @Test
    fun `when joinGroup with invalid invite code then should return failure`() = runTest {
        val request = JoinGroupRequest("INVALIDCODE")
        val error = Exception("Invalid invite code")
        everySuspend { remoteDataSource.joinGroup(request) } returns Result.failure(error)

        val result = repository.joinGroup(request)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { remoteDataSource.joinGroup(request) }
    }

    @Test
    fun `when getRankingMembers with valid group id then should return success with members list`() = runTest {
        val groupId = 1
        everySuspend { remoteDataSource.getRankingMembers(groupId) } returns Result.success(testRankingMembers)

        val result = repository.getRankingMembers(groupId)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Alice", result.getOrNull()?.get(0)?.displayName)
        verifySuspend { remoteDataSource.getRankingMembers(groupId) }
    }

    @Test
    fun `when getRankingMembers and remote returns empty list then should return success with empty list`() = runTest {
        val groupId = 1
        everySuspend { remoteDataSource.getRankingMembers(groupId) } returns Result.success(emptyList())

        val result = repository.getRankingMembers(groupId)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        verifySuspend { remoteDataSource.getRankingMembers(groupId) }
    }

    @Test
    fun `when getRankingMembers and remote throws exception then should return failure`() = runTest {
        val groupId = 1
        val error = Exception("Failed to fetch ranking")
        everySuspend { remoteDataSource.getRankingMembers(groupId) } returns Result.failure(error)

        val result = repository.getRankingMembers(groupId)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { remoteDataSource.getRankingMembers(groupId) }
    }

    @Test
    fun `when registerWorkoutLog with valid request then should return success`() = runTest {
        val request = WorkoutLogRequest(WorkoutType.GYM, "Gym session", 3600L)
        everySuspend { remoteDataSource.registerWorkoutLog(request) } returns Result.success(Unit)

        val result = repository.registerWorkoutLog(request)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        verifySuspend { remoteDataSource.registerWorkoutLog(request) }
    }

    @Test
    fun `when registerWorkoutLog and remote throws exception then should return failure`() = runTest {
        val request = WorkoutLogRequest(WorkoutType.RUNNING, "Run", 1800L)
        val error = Exception("Failed to register workout")
        everySuspend { remoteDataSource.registerWorkoutLog(request) } returns Result.failure(error)

        val result = repository.registerWorkoutLog(request)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { remoteDataSource.registerWorkoutLog(request) }
    }

    @Test
    fun `when registerWorkoutLog with null optional fields then should return success`() = runTest {
        val request = WorkoutLogRequest(WorkoutType.OTHER, null, null)
        everySuspend { remoteDataSource.registerWorkoutLog(request) } returns Result.success(Unit)

        val result = repository.registerWorkoutLog(request)

        assertTrue(result.isSuccess)
        verifySuspend { remoteDataSource.registerWorkoutLog(request) }
    }
}

