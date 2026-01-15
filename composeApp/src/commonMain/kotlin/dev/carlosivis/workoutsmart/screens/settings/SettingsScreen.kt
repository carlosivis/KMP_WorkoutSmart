package dev.carlosivis.workoutsmart.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(Unit){
        action(SettingsViewAction.GetSettings)
    }

    Content(
        state = state,
        action = action
    )
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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ){
            // Theme Section
            SectionTitle("Tema")
            ThemeSection(state, action)

            Spacer(Modifier.size(Dimens.Large))

            // Rest Time Section
            SectionTitle("Tempo de Descanso Padrão")
            RestTimeSection(state, action)

            Spacer(Modifier.size(Dimens.Large))

            // Additional Settings Section
            SectionTitle("Configurações Adicionais")
            AdditionalSettingsSection(state, action)

            Spacer(Modifier.size(Dimens.Large))

            // Save Button
            Button(
                onClick = { action(SettingsViewAction.SaveSettings) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Medium)
            ) {
                Text(stringResource(Res.string.action_save))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = Dimens.Medium, vertical = Dimens.Small)
    )
}

@Composable
fun ThemeSection(
    state: SettingsViewState,
    action: (SettingsViewAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
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
}

@Composable
fun RestTimeSection(
    state: SettingsViewState,
    action: (SettingsViewAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text("${state.settings.defaultRestSeconds} segundos")
            }
            Spacer(Modifier.size(Dimens.Small))
            Slider(
                value = state.settings.defaultRestSeconds.toFloat(),
                onValueChange = {
                    action(SettingsViewAction.UpdateDefaultRestTime(it.toInt()))
                },
                valueRange = 5f..300f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AdditionalSettingsSection(
    state: SettingsViewState,
    action: (SettingsViewAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            // Keep Screen On
            SettingOption(
                label = "Manter Tela Ativa",
                description = "Mantém a tela ligada durante os treinos",
                checked = state.settings.keepScreenOn,
                onCheckedChange = { action(SettingsViewAction.UpdateKeepScreenOn(it)) }
            )
            Spacer(Modifier.size(Dimens.Medium))
            // Vibration
            SettingOption(
                label = "Vibração",
                description = "Ativar feedback de vibração",
                checked = state.settings.vibrationEnabled,
                onCheckedChange = { action(SettingsViewAction.UpdateVibrationEnabled(it)) }
            )
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = Dimens.Medium, horizontal = Dimens.Small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.size(Dimens.Small))
        Text(text = label)
    }
}

@Composable
fun SettingOption(
    label: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            if (description != null) {
                Spacer(Modifier.size(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }

}