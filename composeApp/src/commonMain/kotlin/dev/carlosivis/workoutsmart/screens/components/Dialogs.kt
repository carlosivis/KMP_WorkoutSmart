package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import org.jetbrains.compose.resources.stringResource


@Composable
fun CustomDialog(
    title: String,
    message: String?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmButtonText: String? = null,
    cancelButtonText: String? = null
) {
    Dialog(onDismissRequest = onCancel) {
        Card {
            Column(
                modifier = Modifier
                    .padding(Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens.Medium))
                Text(
                    text = message ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(Dimens.Medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Medium, Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text(cancelButtonText?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.action_cancel))
                    }
                    Button(
                        onClick = onConfirm
                    ) {
                        Text(confirmButtonText?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.action_confirm))
                    }
                }
            }
        }
    }
}
