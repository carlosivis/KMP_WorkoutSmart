package dev.carlosivis.workoutsmart.screens.social.groups

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme

@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (GroupsViewAction) -> Unit = viewModel::dispatchAction

    Content(state, action)

}

@Composable
fun Content(
    state: GroupsViewState,
    action: (GroupsViewAction) -> Unit,
) {
    Scaffold() { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyRow (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
//                items(
//                    items = state.groups,
//                ){
//                    GroupCard(group = it, onClick = {})
//                }
            }
        }
    }
}

@Composable
fun GroupCard(group: GroupResponse, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {

        val initials = remember(group.name) {
            group.name.trim()
                .split("\\s+".toRegex())
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
                .ifEmpty { "?" }
        }
        Column(
            modifier = Modifier.padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(Dimens.ImageSizeLarge),
                shape = CircleShape,
                shadowElevation = Dimens.Medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = Dimens.ExtraSmall,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (initials.length > 1) FontSizes.HeadlineMedium else FontSizes.HeadlineLarge
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Small))
            Text(text = group.name,
                style = MaterialTheme.typography.titleLarge,
                )
            Spacer(modifier = Modifier.height(Dimens.Small))
            Text(text = "Rank: #${group.userPosition}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

}

@Preview
@Composable
fun GroupCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        GroupCard(
            group = GroupResponse(
                id = 1,
                name = "Teste Nome",
                inviteCode = "123456",
                userScore = 100,
                userPosition = 1
            ),
            onClick = {}
        )
    }
}