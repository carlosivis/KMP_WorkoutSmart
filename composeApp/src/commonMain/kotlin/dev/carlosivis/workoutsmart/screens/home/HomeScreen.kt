
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.Utils.Shapes
import dev.carlosivis.workoutsmart.screens.home.HomeViewAction
import dev.carlosivis.workoutsmart.screens.home.HomeViewModel
import dev.carlosivis.workoutsmart.screens.home.HomeViewState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
){
    val state by viewModel.state.collectAsState()
    val action: (HomeViewAction) -> Unit = viewModel::dispatchAction
    Content(
        state = state,
        action = viewModel::dispatchAction
    )
    LaunchedEffect(Unit) {
        action(HomeViewAction.GetWorkouts)
        action(HomeViewAction.GetHistory)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun  Content(
    state: HomeViewState,
    action : (HomeViewAction) -> Unit){
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { action(HomeViewAction.Navigate.CreateWorkout)},
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(Dimens.Medium),
                shape = RoundedCornerShape(Shapes.ExtraLarge)
            ) {
                Icon(Icons.Filled.Add, "Criar Treino")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Workout Smart", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(Dimens.Medium)
        ) {
            Text(
                "Treinos Salvos",
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
                            workout.name,
                            modifier = Modifier.padding(Dimens.Large),
                            fontSize = FontSizes.BodyLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.Large))
            Text(
                "Histórico de Treinos",
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
                            history.workoutName + " - " + history.date,
                            modifier = Modifier.padding(Dimens.Large),
                            fontSize = FontSizes.BodyLarge
                        )
                    }
                }
            }
        }
    }
}