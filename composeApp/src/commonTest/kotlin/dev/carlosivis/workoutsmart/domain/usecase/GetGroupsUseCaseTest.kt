package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
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
class GetGroupsUseCaseTest {

    private val repository = mock<SocialRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetGroupsUseCase

    private val testGroups = listOf(
        GroupResponse(1, "Group 1", "CODE1", 100L, 1),
        GroupResponse(2, "Group 2", "CODE2", 50L, 2)
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetGroupsUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute then should return success with list of groups`() = runTest {
        everySuspend { repository.getGroups() } returns Result.success(testGroups)

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Group 1", result.getOrNull()?.get(0)?.name)
        assertEquals("Group 2", result.getOrNull()?.get(1)?.name)
        verifySuspend { repository.getGroups() }
    }

    @Test
    fun `when execute and repository returns empty list then should return success with empty list`() = runTest {
        everySuspend { repository.getGroups() } returns Result.success(emptyList())

        val result = useCase(Unit)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        verifySuspend { repository.getGroups() }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Network error")
        everySuspend { repository.getGroups() } returns Result.failure(error)

        val result = useCase(Unit)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.getGroups() }
    }

    @Test
    fun `when execute multiple times then should call repository each time`() = runTest {
        everySuspend { repository.getGroups() } returns Result.success(testGroups)

        val result1 = useCase(Unit)
        val result2 = useCase(Unit)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())
    }
}

