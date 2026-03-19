package dev.carlosivis.workoutsmart.screens.social.ranking

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.domain.usecase.GetRankingMembersUseCase
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.RankingMember
import dev.carlosivis.workoutsmart.navigation.navigator.RankingNavigator
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RankingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val socialRepository = mock<SocialRepository>()
    private val getRankingUseCase = GetRankingMembersUseCase(socialRepository, testDispatcher)
    private val navigator = mock<RankingNavigator>(MockMode.autofill)

    private val testGroup = GroupResponse(
        id = 1,
        name = "Test Group",
        inviteCode = "ABC123",
        userScore = 100L,
        userPosition = 1
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then should fetch ranking members and sort them`() = runTest {
        val rankingMembers = listOf(
            RankingMember(3, "Third", "photo3.jpg", 30L),
            RankingMember(1, "First", "photo1.jpg", 100L),
            RankingMember(2, "Second", "photo2.jpg", 50L)
        )

        everySuspend { socialRepository.getRankingMembers(testGroup.id) } returns Result.success(
            rankingMembers
        )

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.podium.size)
            assertEquals("First", state.podium[0].displayName)
            assertEquals("Second", state.podium[1].displayName)
            assertEquals("Third", state.podium[2].displayName)
            assertEquals(testGroup, state.group)
        }
    }

    @Test
    fun `when ranking has more than 3 members then podium should contain only top 3`() = runTest {
        val rankingMembers = listOf(
            RankingMember(1, "First", "photo1.jpg", 100L),
            RankingMember(2, "Second", "photo2.jpg", 90L),
            RankingMember(3, "Third", "photo3.jpg", 80L),
            RankingMember(4, "Fourth", "photo4.jpg", 70L),
            RankingMember(5, "Fifth", "photo5.jpg", 60L)
        )

        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(rankingMembers)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.podium.size)
            assertEquals(2, state.others.size)
            assertEquals("First", state.podium[0].displayName)
            assertEquals("Fourth", state.others[0].displayName)
            assertEquals("Fifth", state.others[1].displayName)
        }
    }

    @Test
    fun `when dispatch GetRanking then should update state with sorted members`() = runTest {
        val rankingMembers = listOf(
            RankingMember(2, "Second", "photo2.jpg", 50L),
            RankingMember(1, "First", "photo1.jpg", 100L)
        )

        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(rankingMembers)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(RankingViewAction.GetRanking)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.podium.size)
            assertEquals("First", state.podium[0].displayName)
            assertEquals("Second", state.podium[1].displayName)
        }
        verifySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        }
    }

    @Test
    fun `when ranking fetch fails then should update error state`() = runTest {
        val error = Exception("Network error")
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.failure(error)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Network error", state.error)
            assertEquals(0, state.podium.size)
            assertEquals(0, state.others.size)
        }
    }

    @Test
    fun `when dispatch ShowInviteCode then should toggle visibility`() = runTest {
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(emptyList())

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showInviteCode)
        }

        viewModel.dispatchAction(RankingViewAction.ShowInviteCode)

        viewModel.state.test {
            assertTrue(awaitItem().showInviteCode)
        }

        viewModel.dispatchAction(RankingViewAction.ShowInviteCode)

        viewModel.state.test {
            assertFalse(awaitItem().showInviteCode)
        }
    }

    @Test
    fun `when dispatch CopyInviteCode then should set success message`() = runTest {
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(emptyList())

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(RankingViewAction.CopyInviteCode)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Código copiado para a área de transferência!", state.message)
        }
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val error = Exception("Error")
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.failure(error)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Error", state.error)
        }

        viewModel.dispatchAction(RankingViewAction.CleanMessages)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.error)
            assertEquals(null, state.message)
        }
    }

    @Test
    fun `when dispatch Navigate Back then should call navigator`() = runTest {
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(emptyList())

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)

        viewModel.dispatchAction(RankingViewAction.Navigate.Back)

        verify { navigator.back() }
    }

    @Test
    fun `when dispatch Refresh then should fetch ranking again`() = runTest {
        val rankingMembers = listOf(
            RankingMember(1, "First", "photo1.jpg", 100L)
        )

        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(rankingMembers)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(RankingViewAction.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
        }
        verifySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        }
    }

    @Test
    fun `when empty ranking then podium and others should be empty`() = runTest {
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(emptyList())

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.podium.size)
            assertEquals(0, state.others.size)
        }
    }

    @Test
    fun `when single member then podium should have one member`() = runTest {
        val rankingMembers = listOf(
            RankingMember(1, "Solo", "photo.jpg", 100L)
        )

        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(rankingMembers)

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.podium.size)
            assertEquals(0, state.others.size)
        }
    }

    @Test
    fun `when group info is set then state should contain group data`() = runTest {
        everySuspend {
            socialRepository.getRankingMembers(testGroup.id)
        } returns Result.success(emptyList())

        val viewModel = RankingViewModel(testGroup, getRankingUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testGroup.id, state.group?.id)
            assertEquals(testGroup.name, state.group?.name)
            assertEquals(testGroup.inviteCode, state.group?.inviteCode)
        }
    }
}

