package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.carlosivis.workoutsmart.screens.social.groups.GroupsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GroupsComponent (
    componentContext: ComponentContext,
    val navigator: GroupsNavigator
) : ComponentContext by componentContext, KoinComponent {
    val viewModel = GroupsViewModel(
        groups = get(),
        GetGroupsUseCase = get(),
        CreateGroupUseCase = get(),
        JoinGroupUseCase = get(),
        navigator = navigator)

}