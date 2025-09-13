package dev.carlosivis.workoutsmart.screens.activeWorkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.Utils.WhitePure
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.exercise_default
import dev.carlosivis.workoutsmart.models.ExerciseModel
import dev.carlosivis.workoutsmart.screens.components.CustomDialog
import org.jetbrains.compose.resources.painterResource


@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
) {
    val state by viewModel.state.collectAsState()
    val action: (ActiveWorkoutViewAction) -> Unit = viewModel::dispatchAction

    LaunchedEffect(Unit) {
        action(ActiveWorkoutViewAction.GetWorkout)
    }
    Content(
        state = state,
        action = action
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Content(
    state: ActiveWorkoutViewState,
    action: (ActiveWorkoutViewAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workout.name) },
                navigationIcon = {
                    IconButton(onClick = { action(ActiveWorkoutViewAction.AttemptToNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column {
                if (!state.isWorkoutActive) {
                    Button(
                        onClick = { action(ActiveWorkoutViewAction.StartWorkout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Medium)
                    ) {
                        Text("Iniciar Treino")
                    }
                } else {
                    Text(
                        text = "Tempo decorrido: ${state.elapsedTime}s",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.Medium),
                        fontSize = FontSizes.BodyLarge
                    )
                }

                RestTimeSelector(
                    selectedTime = state.restTime,
                    onTimeSelected = { action(ActiveWorkoutViewAction.UpdateRestTime(it)) }
                )

                val lazyListState = rememberLazyListState()
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
                    contentPadding = PaddingValues(horizontal = Dimens.Medium)
                ) {
                    items(state.workout.exercises) {
                        ExerciseCard(
                            exercise = it,
                            restTimer = { action(ActiveWorkoutViewAction.StartTimer) }
                        )
                    }
                }
            }

            if (state.isRestTimerActive) {
                RestTimerCard(
                    time = state.restTimerValue,
                    onStop = { action(ActiveWorkoutViewAction.StopTimer) }
                )
            }
        }

        if (state.showExitConfirmationDialog) {
            CustomDialog(
                title = "Sair sem salvar?",
                message = "Você ainda não terminou o treino. Deseja realmente sair?",
                onConfirm = { action(ActiveWorkoutViewAction.NavigateBack) },
                onCancel = { action(ActiveWorkoutViewAction.CancelNavigateBack) }
            )
        }
    }
}

@Composable
private fun RestTimeSelector(
    selectedTime: Int,
    onTimeSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tempo de descanso:", fontSize = FontSizes.BodyMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Small)
        ) {
            listOf(30, 60, 90, 120).forEach { seconds ->
                FilterChip(
                    selected = selectedTime == seconds,
                    onClick = { onTimeSelected(seconds) },
                    label = { Text("${seconds}s") }
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseModel,
    restTimer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .width(420.dp)
            .padding(horizontal = Dimens.Small)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.name,
                        fontSize = FontSizes.TitleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = Dimens.Small),
                        textAlign = TextAlign.Center )
                    TextButton(onClick = { /*TODO change visibility of card*/ }) {
                        Text("Concluido", fontSize = FontSizes.BodySmall)
                    }
                }
                //TODO("adds check if exercise have image")
                Image(
                    painter = painterResource(Res.drawable.exercise_default),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(Dimens.Medium)),
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = restTimer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Descanso")
            }
        }
    }
}

@Composable
private fun RestTimerCard(time: Int, onStop: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(percent = 100))
                .background(Color.DarkGray)
                .border(BorderStroke(2.dp, WhitePure), RoundedCornerShape(percent = 100)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = time.toString(),
                    fontSize = FontSizes.HeadlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = Dimens.Medium)
                )
                Button(onClick = onStop) {
                    Text("Pular")
                }
            }
        }
    }

}