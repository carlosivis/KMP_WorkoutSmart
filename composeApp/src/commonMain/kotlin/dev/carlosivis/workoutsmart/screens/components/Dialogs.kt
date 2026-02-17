package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_cancel
import dev.carlosivis.workoutsmart.composeResources.action_confirm
import dev.carlosivis.workoutsmart.composeResources.create_group_dialog_create_button
import dev.carlosivis.workoutsmart.composeResources.create_group_dialog_description_label
import dev.carlosivis.workoutsmart.composeResources.create_group_dialog_name_label
import dev.carlosivis.workoutsmart.composeResources.create_group_dialog_title
import dev.carlosivis.workoutsmart.composeResources.join_group_dialog_invite_code_label
import dev.carlosivis.workoutsmart.composeResources.join_group_dialog_join_button
import dev.carlosivis.workoutsmart.composeResources.join_group_dialog_title
import dev.carlosivis.workoutsmart.models.CreateGroupRequest
import dev.carlosivis.workoutsmart.models.JoinGroupRequest
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.Shapes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
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
        Card(
            shape = RoundedCornerShape(Shapes.Large),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
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
@Composable
private fun BaseDialogContent(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit,
    actions: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(Shapes.Large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.Small),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.ImageSizeSmall)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.Large)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Medium))

            // Título
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimens.Large))

            // Conteúdo (Inputs)
            content()

            Spacer(modifier = Modifier.height(Dimens.Large))

            // Botões
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End, // Botões à direita é padrão UX
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions()
            }
        }
    }
}

@Composable
fun CustomJoinGroupDialog(
    onDismiss: () -> Unit = {},
    onConfirm: (JoinGroupRequest) -> Unit,
    onCancel: () -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        BaseDialogContent(
            icon = Icons.Default.Key,
            title = stringResource(Res.string.join_group_dialog_title),
            content = {
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it },
                    label = { Text(stringResource(Res.string.join_group_dialog_invite_code_label)) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.Medium),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
            },
            actions = {
                TextButton(onClick = onCancel) {
                    Text(
                        stringResource(Res.string.action_cancel),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.Small))
                Button(
                    onClick = {
                        if (inviteCode.isNotBlank()) {
                            onConfirm(JoinGroupRequest(inviteCode = inviteCode.trim()))
                        }
                    },
                    enabled = inviteCode.isNotBlank(),
                    shape = RoundedCornerShape(Dimens.Medium)
                ) {
                    Text(stringResource(Res.string.join_group_dialog_join_button))
                }
            }
        )
    }
}

@Composable
fun CustomCreateGroupDialog(
    onDismiss: () -> Unit = {},
    onConfirm: (CreateGroupRequest) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        BaseDialogContent(
            icon = Icons.Default.GroupAdd,
            title = stringResource(Res.string.create_group_dialog_title),
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Medium)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(Res.string.create_group_dialog_name_label)) },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.Medium),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(Res.string.create_group_dialog_description_label)) },
                        maxLines = 3,
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.Medium),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            actions = {
                TextButton(onClick = onCancel) {
                    Text(
                        stringResource(Res.string.action_cancel),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.Small))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onConfirm(
                                CreateGroupRequest(
                                    name = name.trim(),
                                    description = description.trim().ifBlank { null }
                                )
                            )
                        }
                    },
                    enabled = name.isNotBlank(),
                    shape = RoundedCornerShape(Dimens.Medium)
                ) {
                    Text(stringResource(Res.string.create_group_dialog_create_button))
                }
            }
        )
    }
}


@Preview
@Composable
fun CustomDialogPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        CustomDialog(title = "teste", message = "teste", onConfirm = {}, onCancel = {})
    }
}

@Preview
@Composable
fun CustomJoinGroupDialogPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        CustomJoinGroupDialog(onCancel = {}, onConfirm = {})
    }
}

@Preview
@Composable
fun CustomCreateGroupDialogPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        CustomCreateGroupDialog(onCancel = {}, onConfirm = {})
    }
}
