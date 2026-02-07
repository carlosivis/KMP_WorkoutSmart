package dev.carlosivis.workoutsmart.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.create_workout_fab
import dev.carlosivis.workoutsmart.composeResources.delete_action
import dev.carlosivis.workoutsmart.composeResources.delete_workout_message
import dev.carlosivis.workoutsmart.composeResources.delete_workout_title
import dev.carlosivis.workoutsmart.composeResources.edit_action
import dev.carlosivis.workoutsmart.composeResources.home_screen_duration
import dev.carlosivis.workoutsmart.composeResources.home_screen_login
import dev.carlosivis.workoutsmart.composeResources.home_screen_my_profile
import dev.carlosivis.workoutsmart.composeResources.home_screen_title
import dev.carlosivis.workoutsmart.composeResources.saved_workouts_section_title
import dev.carlosivis.workoutsmart.composeResources.workout_history_section_title
import dev.carlosivis.workoutsmart.models.GroupResponse
import dev.carlosivis.workoutsmart.models.HistoryModel
import dev.carlosivis.workoutsmart.models.WorkoutModel
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import dev.carlosivis.workoutsmart.utils.BronzeGradient
import dev.carlosivis.workoutsmart.utils.DefaultRankColor
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.GoldGradient
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.SilverGradient
import dev.carlosivis.workoutsmart.utils.WhitePure
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.utils.formatDateToString
import dev.carlosivis.workoutsmart.utils.formatDuration
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (HomeViewAction) -> Unit = viewModel::dispatchAction
    Content(
        state = state,
        action = viewModel::dispatchAction
    )
    LaunchedEffect(Unit) {
        action(HomeViewAction.GetWorkouts)
        action(HomeViewAction.GetHistory)
        action(HomeViewAction.GetUserProfile)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    state: HomeViewState,
    action: (HomeViewAction) -> Unit
) {
    if (state.workoutToDelete != null) {
        CustomDialog(
            title = stringResource(Res.string.delete_workout_title),
            message = stringResource(Res.string.delete_workout_message, state.workoutToDelete.name),
            onConfirm = { action(HomeViewAction.ConfirmDeleteWorkout) },
            onCancel = { action(HomeViewAction.CancelDeleteWorkout) }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { action(HomeViewAction.Navigate.CreateWorkout) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(Dimens.Medium),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            ) {
                Icon(Icons.Filled.Add, stringResource(Res.string.create_workout_fab))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.Medium)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(Res.string.home_screen_title), fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, fontSize = FontSizes.TitleMedium
                )

                ProfileTopBarIcon(
                    Modifier.align(Alignment.CenterEnd),
                    state.user != null,
                    { action(HomeViewAction.Navigate.Profile) })

            }
            Text(
                stringResource(Res.string.saved_workouts_section_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Small))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.workouts) { workout ->
                    WorkoutCard(
                        workout = workout,
                        navigate = { action(HomeViewAction.Navigate.Workout(workout)) },
                        delete = { action(HomeViewAction.AttemptDeleteWorkout(workout)) },
                        edit = { action(HomeViewAction.Navigate.Edit(workout)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Large))
            Text(
                stringResource(Res.string.workout_history_section_title),
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(Dimens.Small))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(state.history) { history ->
                    HistoryCard(history = history)
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutModel, navigate: () -> Unit = {},
    delete: () -> Unit = {}, edit: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Small)
            .clickable { navigate() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = Dimens.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                workout.name,
                modifier = Modifier.weight(1f),
                fontSize = FontSizes.BodyLarge
            )

            Row {
                IconButton(onClick = edit) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(Res.string.edit_action)
                    )
                }
                IconButton(onClick = delete) {
                    Icon(
                        Icons.Filled.Delete,
                        stringResource(Res.string.delete_action)
                    )
                }
            }
        }

    }

}

@Composable
private fun HistoryCard(history: HistoryModel) {
    val formattedDate = formatDateToString(history.date)
    val formattedDuration = formatDuration(history.duration)
    Card(
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Small)
    ) {
        Text(
            text = "${history.workoutName}\n$formattedDate ${
                stringResource(
                    Res.string.home_screen_duration,
                    formattedDuration
                )
            }",
            modifier = Modifier.padding(Dimens.Medium),
            fontSize = FontSizes.BodyLarge
        )
    }
}

@Composable
fun ProfileTopBarIcon(modifier: Modifier, isLoggedIn: Boolean, onIconClick: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onIconClick
    ) {
        if (isLoggedIn) {
            Icon(
                Icons.Default.Person,
                contentDescription = stringResource(Res.string.home_screen_my_profile)
            )
        } else {
            Icon(
                Icons.AutoMirrored.Filled.Login,
                contentDescription = stringResource(Res.string.home_screen_login)
            )
        }
    }
}

@Composable
fun RankingCarousel(
    groups: List<GroupResponse>,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {}
) {
    Column {
        if (groups.isNotEmpty()) {
            Text(
                text = "Seu Ranking",
                fontSize = FontSizes.TitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Card(
                modifier = modifier
                    .clickable(enabled = true, onClick = { onCardClick() }),
                shape = RoundedCornerShape(Shapes.ExtraLarge),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            ) {

                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.Medium),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Medium)
                ) {
                    items(groups) { group ->
                        RankingBadgeCard(group)
                    }
                }
            }
        }
    }

}

@Composable
private fun RankingBadgeCard(group: GroupResponse) {
    val (backgroundBrush, iconVec) = when (group.userPosition) {
        1 -> GoldGradient to Icons.Filled.EmojiEvents
        2 -> SilverGradient to Icons.Filled.MilitaryTech
        3 -> BronzeGradient to Icons.Filled.MilitaryTech
        else -> {
            Brush.verticalGradient(
                listOf(DefaultRankColor.copy(alpha = 0.7f), DefaultRankColor)
            ) to Icons.Filled.Star
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Medium),
        shape = RoundedCornerShape(Shapes.ExtraLarge),
        modifier = Modifier
            .width(108.dp)
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = FontSizes.BodySmall),
                    color = WhitePure,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = iconVec,
                        contentDescription = null,
                        tint = WhitePure,
                        modifier = Modifier.size(Dimens.ExtraLarge)
                    )

                    Text(
                        text = "#${group.userPosition}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = FontSizes.HeadlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = WhitePure
                    )
                }

            }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = HomeViewState(
                workouts = listOf(
                    WorkoutModel(
                        id = 1,
                        name = "Workout A",
                        description = "Description A",
                        exercises = emptyList()
                    ),
                    WorkoutModel(
                        id = 2,
                        name = "Workout B",
                        description = "Description B",
                        exercises = emptyList()
                    )
                ),
                history = listOf(
                    HistoryModel(
                        id = 1,
                        date = 1678886400000,
                        workoutName = "Workout A",
                        duration = 3600
                    ),
                    HistoryModel(
                        id = 2,
                        date = 1678972800000,
                        workoutName = "Workout B",
                        duration = 3600
                    )
                )
            ),
            action = {}
        )

    }
}

@Preview
@Composable
private fun RankingBadgeCardPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        RankingCarousel(
            groups = listOf(
                GroupResponse(1, "Group 1", "abc", 100, 1),
                GroupResponse(1, "Group 6", "abc", 100, 6),
                GroupResponse(1, "Group 21", "abc", 100, 2),
                GroupResponse(1, "Group 1", "abc", 100, 3),
                GroupResponse(2, "Group 2", "def", 200, 2)
            )
        )
    }
}
