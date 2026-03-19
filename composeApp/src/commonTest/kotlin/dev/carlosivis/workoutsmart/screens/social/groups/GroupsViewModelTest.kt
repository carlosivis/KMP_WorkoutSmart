package dev.carlosivis.workoutsmart.screens.social.groups

import app.cash.turbine.test
import dev.carlosivis.workoutsmart.domain.repository.SocialRepository
import dev.carlosivis.workoutsmart.domain.usecase.CreateGroupUseCase
import dev.carlosivis.workoutsmart.domain.usecase.GetGroupsUseCase
import dev.carlosivis.workoutsmart.domain.usecase.JoinGroupUseCase
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GroupsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val socialRepository = mock<SocialRepository>()
    private val getGroupsUseCase = GetGroupsUseCase(socialRepository, testDispatcher)
    private val createGroupUseCase = CreateGroupUseCase(socialRepository, testDispatcher)
    private val joinGroupUseCase = JoinGroupUseCase(socialRepository, testDispatcher)
    private val navigator = mock<GroupsNavigator>(MockMode.autofill)


    private val testGroups = listOf(
        GroupResponse(1, "Group 1", "CODE1", 100L, 1),
        GroupResponse(2, "Group 2", "CODE2", 50L, 2)
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
    fun `when init with no initial groups then should fetch groups from repository`() = runTest {
        everySuspend { socialRepository.getGroups() } returns Result.success(testGroups)

        val viewModel =
            GroupsViewModel(null, getGroupsUseCase, createGroupUseCase, joinGroupUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.groups?.size)
            assertEquals("Group 1", state.groups?.get(0)?.name)
        }
        verifySuspend(VerifyMode.exactly(1)) {
            socialRepository.getGroups()
        }
    }

    @Test
    fun `when init with initial groups then should not fetch from repository`() = runTest {
        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.groups?.size)
            assertEquals("Group 1", state.groups?.get(0)?.name)
        }

        verifySuspend(VerifyMode.not) { socialRepository.getGroups() }
    }

    @Test
    fun `when dispatch GetGroups then should fetch from repository`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel =
            GroupsViewModel(null, getGroupsUseCase, createGroupUseCase, joinGroupUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.GetGroups)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.groups?.size)
        }
        verifySuspend(VerifyMode.exactly(2)) {
            socialRepository.getGroups()
        }
    }

    @Test
    fun `when get groups fails then should set error state`() = runTest {
        val error = Exception("Network error")
        everySuspend { socialRepository.getGroups() } returns Result.failure(error)

        val viewModel =
            GroupsViewModel(null, getGroupsUseCase, createGroupUseCase, joinGroupUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Network error", state.error)
        }
    }

    @Test
    fun `when dispatch CreateGroup then should call usecase and navigate to ranking`() = runTest {
        val newGroup = GroupResponse(3, "New Group", "CODE3", 0L, 999)
        val createRequest = CreateGroupRequest("New Group", "Description")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.createGroup(createRequest) } returns Result.success(newGroup)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.CreateGroup(createRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.groups?.size)
            assertEquals("New Group", state.groups?.get(2)?.name)
        }

        verify { navigator.toRanking(newGroup) }
        verifySuspend { socialRepository.createGroup(createRequest) }
    }

    @Test
    fun `when create group fails then should set error state`() = runTest {
        val error = Exception("Creation failed")
        val createRequest = CreateGroupRequest("New Group")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.createGroup(createRequest) } returns Result.failure(error)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.CreateGroup(createRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Creation failed", state.error)
        }
    }

    @Test
    fun `when dispatch JoinGroup then should call usecase and navigate to ranking`() = runTest {
        val joinedGroup = GroupResponse(3, "Joined Group", "CODE3", 0L, 999)
        val joinRequest = JoinGroupRequest("CODE3")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.joinGroup(joinRequest) } returns Result.success(joinedGroup)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.JoinGroup(joinRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.groups?.size)
            assertEquals("Joined Group", state.groups?.get(2)?.name)
        }

        verify { navigator.toRanking(joinedGroup) }
        verifySuspend { socialRepository.joinGroup(joinRequest) }
    }

    @Test
    fun `when join group fails then should set error state`() = runTest {
        val error = Exception("Invalid code")
        val joinRequest = JoinGroupRequest("INVALID")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.joinGroup(joinRequest) } returns Result.failure(error)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.JoinGroup(joinRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Invalid code", state.error)
        }
    }

    @Test
    fun `when dispatch ShowAddGroup then should toggle visibility`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddGroup)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddGroup)

        viewModel.state.test {
            assertTrue(awaitItem().showAddGroup)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddGroup)

        viewModel.state.test {
            assertFalse(awaitItem().showAddGroup)
        }
    }

    @Test
    fun `when dispatch ShowAddInvite then should toggle visibility`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddInvite)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddInvite)

        viewModel.state.test {
            assertTrue(awaitItem().showAddInvite)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddInvite)

        viewModel.state.test {
            assertFalse(awaitItem().showAddInvite)
        }
    }

    @Test
    fun `when dispatch CleanMessages then should clear error and message`() = runTest {
        val error = Exception("Error")
        everySuspend {
            socialRepository.getGroups()
        } returns Result.failure(error)

        val viewModel =
            GroupsViewModel(null, getGroupsUseCase, createGroupUseCase, joinGroupUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertNotNull(awaitItem().error)
        }

        viewModel.dispatchAction(GroupsViewAction.CleanMessages)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            assertNull(state.message)
        }
    }

    @Test
    fun `when dispatch Navigate Back then should call navigator`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )

        viewModel.dispatchAction(GroupsViewAction.Navigate.Back)

        verify { navigator.back() }
    }

    @Test
    fun `when dispatch Navigate Ranking then should call navigator with group`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )

        viewModel.dispatchAction(GroupsViewAction.Navigate.Ranking(testGroups[0]))

        verify { navigator.toRanking(testGroups[0]) }
    }

    @Test
    fun `when dispatch Refresh then should fetch groups again`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
        }
        verifySuspend(VerifyMode.exactly(1)) {
            socialRepository.getGroups()
        }
    }

    @Test
    fun `when multiple groups created then should maintain order`() = runTest {
        val newGroup1 = GroupResponse(3, "Group 3", "CODE3", 0L, 999)
        val newGroup2 = GroupResponse(4, "Group 4", "CODE4", 0L, 999)

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.createGroup(any()) } sequentiallyReturns listOf(
            Result.success(newGroup1),
            Result.success(newGroup2)
        )

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dispatchAction(GroupsViewAction.CreateGroup(CreateGroupRequest("Group 3")))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.groups?.size)
        }

        viewModel.dispatchAction(GroupsViewAction.CreateGroup(CreateGroupRequest("Group 4")))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(4, state.groups?.size)
            assertEquals("Group 1", state.groups?.get(0)?.name)
            assertEquals("Group 4", state.groups?.get(3)?.name)
        }
    }

    @Test
    fun `when empty groups list then state should reflect empty state`() = runTest {
        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(emptyList())

        val viewModel =
            GroupsViewModel(null, getGroupsUseCase, createGroupUseCase, joinGroupUseCase, navigator)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(0, state.groups?.size)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `when create group then dialogs should be toggled`() = runTest {
        val newGroup = GroupResponse(3, "New Group", "CODE3", 0L, 999)
        val createRequest = CreateGroupRequest("New Group")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.createGroup(createRequest) } returns Result.success(newGroup)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddGroup)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddGroup)

        viewModel.state.test {
            assertTrue(awaitItem().showAddGroup)
        }

        viewModel.dispatchAction(GroupsViewAction.CreateGroup(createRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddGroup)
        }
    }

    @Test
    fun `when join group then dialogs should be toggled`() = runTest {
        val joinedGroup = GroupResponse(3, "Joined Group", "CODE3", 0L, 999)
        val joinRequest = JoinGroupRequest("CODE3")

        everySuspend {
            socialRepository.getGroups()
        } returns Result.success(testGroups)
        everySuspend { socialRepository.joinGroup(joinRequest) } returns Result.success(joinedGroup)

        val viewModel = GroupsViewModel(
            testGroups,
            getGroupsUseCase,
            createGroupUseCase,
            joinGroupUseCase,
            navigator
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddInvite)
        }

        viewModel.dispatchAction(GroupsViewAction.ShowAddInvite)

        viewModel.state.test {
            assertTrue(awaitItem().showAddInvite)
        }

        viewModel.dispatchAction(GroupsViewAction.JoinGroup(joinRequest))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertFalse(awaitItem().showAddInvite)
        }
    }
}

