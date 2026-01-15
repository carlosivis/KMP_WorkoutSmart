package dev.carlosivis.workoutsmart.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import dev.carlosivis.workoutsmart.Utils.Dimens
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_save
import dev.carlosivis.workoutsmart.repository.ThemeMode
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
){
    val state by viewModel.state.collectAsState()
    val action: (SettingsViewAction) -> Unit = viewModel::dispatchAction

    Content(
        state = state,
        action = action
    )
    LaunchedEffect(Unit){
        action(SettingsViewAction.GetSettings)
    }
}

@Composable
fun Content(
    state: SettingsViewState,
    action: (SettingsViewAction) -> Unit
){

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(Dimens.Medium)
                .fillMaxWidth()
        ){
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(Modifier.selectableGroup()) {
                    ThemeOption(
                        label = "Automático (Sistema)",
                        icon = Icons.Default.BrightnessMedium,
                        selected = state.settings.themeMode == ThemeMode.SYSTEM,
                        onClick = { action(SettingsViewAction.UpdateThemeMode(ThemeMode.SYSTEM)) }
                    )
                    ThemeOption(
                        label = "Modo Claro",
                        icon = Icons.Default.LightMode,
                        selected = state.settings.themeMode == ThemeMode.LIGHT,
                        onClick = { action(SettingsViewAction.UpdateThemeMode(ThemeMode.LIGHT)) }
                    )
                    ThemeOption(
                        label = "Modo Escuro",
                        icon = Icons.Default.DarkMode,
                        selected = state.settings.themeMode == ThemeMode.DARK,
                        onClick = { action(SettingsViewAction.UpdateThemeMode(ThemeMode.DARK)) }
                    )
                }
            }

            Button(
                onClick = {
                    action(SettingsViewAction.SaveSettings)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.action_save))
            }
        }
    }
}

@Composable
fun ThemeOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = Dimens.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.size(Dimens.Medium))
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = null
        )
    }
}