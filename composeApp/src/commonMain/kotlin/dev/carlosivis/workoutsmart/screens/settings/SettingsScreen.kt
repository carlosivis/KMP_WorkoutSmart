package dev.carlosivis.workoutsmart.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.carlosivis.workoutsmart.utils.Dimens
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.settings_keep_screen_on_description
import dev.carlosivis.workoutsmart.composeResources.settings_keep_screen_on_label
import dev.carlosivis.workoutsmart.composeResources.settings_rest_time_seconds
import dev.carlosivis.workoutsmart.composeResources.settings_screen_title
import dev.carlosivis.workoutsmart.composeResources.settings_section_additional_settings
import dev.carlosivis.workoutsmart.composeResources.settings_section_default_rest_time
import dev.carlosivis.workoutsmart.composeResources.settings_section_theme
import dev.carlosivis.workoutsmart.composeResources.settings_theme_option_dark
import dev.carlosivis.workoutsmart.composeResources.settings_theme_option_light
import dev.carlosivis.workoutsmart.composeResources.settings_theme_option_system
import dev.carlosivis.workoutsmart.composeResources.settings_vibration_description
import dev.carlosivis.workoutsmart.composeResources.settings_vibration_label
import dev.carlosivis.workoutsmart.models.SettingsModel
import dev.carlosivis.workoutsmart.repository.ThemeMode
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action: (SettingsViewAction) -> Unit = viewModel::dispatchAction

    LaunchedEffect(Unit) {
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
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { action(SettingsViewAction.NavigateBack) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.action_back)
                    )
                }
                Text(
                    text = stringResource(Res.string.settings_screen_title),
                    fontSize = FontSizes.TitleMedium,
                    textAlign = TextAlign.Center
                )
            }

            SectionTitle(stringResource(Res.string.settings_section_theme))
            ThemeSection(state, action)

            Spacer(Modifier.size(Dimens.Large))

            SectionTitle(stringResource(Res.string.settings_section_default_rest_time))
            RestTimeSection(state, action)

            Spacer(Modifier.size(Dimens.Large))

            SectionTitle(stringResource(Res.string.settings_section_additional_settings))
            AdditionalSettingsSection(state, action)
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
                label = stringResource(Res.string.settings_theme_option_system),
                icon = Icons.Default.BrightnessMedium,
                selected = state.settings.themeMode == ThemeMode.SYSTEM,
                onClick = { action(SettingsViewAction.UpdateThemeMode(ThemeMode.SYSTEM)) }
            )
            ThemeOption(
                label = stringResource(Res.string.settings_theme_option_light),
                icon = Icons.Default.LightMode,
                selected = state.settings.themeMode == ThemeMode.LIGHT,
                onClick = { action(SettingsViewAction.UpdateThemeMode(ThemeMode.LIGHT)) }
            )
            ThemeOption(
                label = stringResource(Res.string.settings_theme_option_dark),
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(
                        Res.string.settings_rest_time_seconds,
                        state.settings.defaultRestSeconds
                    )
                )
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
                label = stringResource(Res.string.settings_keep_screen_on_label),
                description = stringResource(Res.string.settings_keep_screen_on_description),
                checked = state.settings.keepScreenOn,
                onCheckedChange = { action(SettingsViewAction.UpdateKeepScreenOn(it)) }
            )
            Spacer(Modifier.size(Dimens.Medium))
            // Vibration
            SettingOption(
                label = stringResource(Res.string.settings_vibration_label),
                description = stringResource(Res.string.settings_vibration_description),
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
        horizontalArrangement = Arrangement.SpaceBetween
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

@Preview
@Composable
private fun ContentPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Content(
            state = SettingsViewState(
                settings = SettingsModel(
                    themeMode = ThemeMode.SYSTEM,
                    defaultRestSeconds = 60,
                    keepScreenOn = true,
                    vibrationEnabled = true
                )
            ),
            action = {}
        )
    }
}
