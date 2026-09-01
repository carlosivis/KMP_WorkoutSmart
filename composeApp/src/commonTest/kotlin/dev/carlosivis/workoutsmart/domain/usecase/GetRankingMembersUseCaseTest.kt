package dev.carlosivis.workoutsmart.domain.usecase

import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
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
class GetRankingMembersUseCaseTest {

    private val repository = mock<SocialRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetRankingMembersUseCase

    private val testGroupId = 1
    private val testRankingMembers = listOf(
        RankingMember(1, "Alice", "https://example.com/alice.jpg", 1000L),
        RankingMember(2, "Bob", "https://example.com/bob.jpg", 900L),
        RankingMember(3, "Charlie", "https://example.com/charlie.jpg", 800L)
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetRankingMembersUseCase(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when execute with group id then should return success with ranking members`() = runTest {
        everySuspend { repository.getRankingMembers(testGroupId) } returns Result.success(testRankingMembers)

        val result = useCase(testGroupId)

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("Alice", result.getOrNull()?.get(0)?.displayName)
        assertEquals(1, result.getOrNull()?.get(0)?.position)
        assertEquals(1000L, result.getOrNull()?.get(0)?.score)
        verifySuspend { repository.getRankingMembers(testGroupId) }
    }

    @Test
    fun `when execute and repository returns empty list then should return success with empty list`() = runTest {
        everySuspend { repository.getRankingMembers(testGroupId) } returns Result.success(emptyList())

        val result = useCase(testGroupId)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
        verifySuspend { repository.getRankingMembers(testGroupId) }
    }

    @Test
    fun `when execute and repository throws exception then should return failure`() = runTest {
        val error = Exception("Failed to fetch ranking")
        everySuspend { repository.getRankingMembers(testGroupId) } returns Result.failure(error)

        val result = useCase(testGroupId)

        assertTrue(result.isFailure)
        assertEquals(error.message, result.exceptionOrNull()?.message)
        verifySuspend { repository.getRankingMembers(testGroupId) }
    }

    @Test
    fun `when execute with different group ids then should pass correct parameters to repository`() = runTest {
        val groupId1 = 1
        val groupId2 = 2
        val members1 = listOf(RankingMember(1, "User1", "url1", 100L))
        val members2 = listOf(RankingMember(1, "User2", "url2", 200L))

        everySuspend { repository.getRankingMembers(groupId1) } returns Result.success(members1)
        everySuspend { repository.getRankingMembers(groupId2) } returns Result.success(members2)

        val result1 = useCase(groupId1)
        val result2 = useCase(groupId2)

        assertEquals(members1, result1.getOrNull())
        assertEquals(members2, result2.getOrNull())
        verifySuspend { repository.getRankingMembers(groupId1) }
        verifySuspend { repository.getRankingMembers(groupId2) }
    }
}

