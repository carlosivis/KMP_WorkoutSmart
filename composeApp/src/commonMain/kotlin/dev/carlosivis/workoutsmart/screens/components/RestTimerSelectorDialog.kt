package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.Utils.FontSizes
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_save
import dev.carlosivis.workoutsmart.composeResources.active_workout_timer_separator
import dev.carlosivis.workoutsmart.composeResources.rest_time_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun RestTimerSelectorDialog(
    currentTime: Int,
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialMinutes = currentTime / 60
    val initialSeconds = currentTime % 60

    var selectedMinutes by remember { mutableStateOf(initialMinutes) }
    var selectedSeconds by remember { mutableStateOf(initialSeconds) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(Dimens.Medium)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Previne fechar ao clicar no card
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Medium)
            ) {
                Text(
                    text = stringResource(Res.string.rest_time_label),
                    fontSize = FontSizes.TitleMedium,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeWheelPicker(
                        range = 0..59,
                        initialValue = selectedMinutes,
                        onValueChange = { selectedMinutes = it }
                    )
                    Text(
                        stringResource(Res.string.active_workout_timer_separator),
                        fontSize = FontSizes.TitleLarge
                    )
                    TimeWheelPicker(
                        range = 0..59,
                        initialValue = selectedSeconds,
                        onValueChange = { selectedSeconds = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            text = stringResource(Res.string.action_cancel),
                        )
                    }
                    TextButton(
                        onClick = {
                            val totalSeconds = selectedMinutes * 60 + selectedSeconds
                            onTimeSelected(totalSeconds)
                            onDismiss() // Fecha após selecionar
                        },
                    ) {
                        Text(
                            text = stringResource(Res.string.action_save),
                        )
                    }
                }
            }
        }
    }
}

