package dev.carlosivis.workoutsmart.navigation

import com.arkivanov.decompose.ComponentContext
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.navigation.navigator.GroupsNavigator
import dev.carlosivis.workoutsmart.screens.social.groups.GroupsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class GroupsComponent (
    componentContext: ComponentContext,
    val navigator: GroupsNavigator,
    val groups: List<GroupResponse>?
) : ComponentContext by componentContext, KoinComponent {
    val viewModel: GroupsViewModel = get { parametersOf(navigator, groups) }
}