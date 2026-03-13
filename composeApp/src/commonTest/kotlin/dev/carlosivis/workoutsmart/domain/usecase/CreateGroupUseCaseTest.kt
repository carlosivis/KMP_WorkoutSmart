package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
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
class CreateGroupUseCaseTest {

    private val repository = mock<SocialRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: CreateGroupUseCase

    private val testCreateGroupRequest = CreateGroupRequest(
        name = "Test Group",
        description = "Test Description"
    )

    private val testGroupResponse = GroupResponse(
        id = 1,
        name = "Test Group",
        inviteCode = "TESTCODE123",
        userScore = 0L,
        userPosition = 1
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = CreateGroupUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute with valid request then should return success with group response`() = runTest {
        everySuspend { repository.createGroup(testCreateGroupRequest) } returns Result.success(testGroupResponse)

        val result = useCase(testCreateGroupRequest)

        assertTrue(result.isSuccess)
        assertEquals(testGroupResponse, result.getOrNull())
        verifySuspend { repository.createGroup(testCreateGroupRequest) }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Failed to create group")
        everySuspend { repository.createGroup(testCreateGroupRequest) } returns Result.failure(error)

        val result = useCase(testCreateGroupRequest)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.createGroup(testCreateGroupRequest) }
    }

    @Test
    fun `when execute with different group names then should pass correct parameters to repository`() = runTest {
        val request1 = CreateGroupRequest("Group A", "Description A")
        val request2 = CreateGroupRequest("Group B", "Description B")
        val response1 = GroupResponse(1, "Group A", "CODE1", 0L, 1)
        val response2 = GroupResponse(2, "Group B", "CODE2", 0L, 1)

        everySuspend { repository.createGroup(request1) } returns Result.success(response1)
        everySuspend { repository.createGroup(request2) } returns Result.success(response2)

        val result1 = useCase(request1)
        val result2 = useCase(request2)

        assertEquals(response1, result1.getOrNull())
        assertEquals(response2, result2.getOrNull())
        verifySuspend { repository.createGroup(request1) }
        verifySuspend { repository.createGroup(request2) }
    }
}

