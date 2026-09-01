package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
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
class JoinGroupUseCaseTest {

    private val repository = mock<SocialRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: JoinGroupUseCase

    private val testJoinRequest = JoinGroupRequest(
        inviteCode = "TESTCODE123"
    )

    private val testGroupResponse = GroupResponse(
        id = 1,
        name = "Test Group",
        inviteCode = "TESTCODE123",
        userScore = 50L,
        userPosition = 5
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = JoinGroupUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute with valid invite code then should return success with group response`() = runTest {
        everySuspend { repository.joinGroup(testJoinRequest) } returns Result.success(testGroupResponse)

        val result = useCase(testJoinRequest)

        assertTrue(result.isSuccess)
        assertEquals(testGroupResponse, result.getOrNull())
        assertEquals("Test Group", result.getOrNull()?.name)
        assertEquals(50L, result.getOrNull()?.userScore)
        verifySuspend { repository.joinGroup(testJoinRequest) }
    }

    @Test
    fun `when execute with invalid invite code then should return failure`() = runTest {
        val invalidRequest = JoinGroupRequest(inviteCode = "INVALIDCODE")
        val error = Exception("Invalid invite code")
        everySuspend { repository.joinGroup(invalidRequest) } returns Result.failure(error)

        val result = useCase(invalidRequest)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.joinGroup(invalidRequest) }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Failed to join group")
        everySuspend { repository.joinGroup(testJoinRequest) } returns Result.failure(error)

        val result = useCase(testJoinRequest)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.joinGroup(testJoinRequest) }
    }

    @Test
    fun `when execute with different invite codes then should pass correct parameters to repository`() = runTest {
        val request1 = JoinGroupRequest("CODE1")
        val request2 = JoinGroupRequest("CODE2")
        val response1 = GroupResponse(1, "Group 1", "CODE1", 100L, 2)
        val response2 = GroupResponse(2, "Group 2", "CODE2", 50L, 3)

        everySuspend { repository.joinGroup(request1) } returns Result.success(response1)
        everySuspend { repository.joinGroup(request2) } returns Result.success(response2)

        val result1 = useCase(request1)
        val result2 = useCase(request2)

        assertEquals(response1, result1.getOrNull())
        assertEquals(response2, result2.getOrNull())
        verifySuspend { repository.joinGroup(request1) }
        verifySuspend { repository.joinGroup(request2) }
    }

    @Test
    fun `when execute then user position should be set in response`() = runTest {
        everySuspend { repository.joinGroup(testJoinRequest) } returns Result.success(testGroupResponse)

        val result = useCase(testJoinRequest)

        val group = result.getOrNull()
        assertEquals(5, group?.userPosition)
    }
}

