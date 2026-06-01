package au.edu.jcu.cp3406.outdoorready.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import au.edu.jcu.cp3406.outdoorready.ui.components.ScreenHeader
import au.edu.jcu.cp3406.outdoorready.ui.components.SettingsOverviewCard
import au.edu.jcu.cp3406.outdoorready.ui.components.SettingsSection
import au.edu.jcu.cp3406.outdoorready.ui.components.ToggleRow

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    val locationFocusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = "Settings",
                subtitle = "Tune what the Ready screen checks and how cautious it should be",
            )
        }

        item {
            SettingsOverviewCard(preferences = state.preferences)
        }

        item {
            SettingsSection(
                title = "Location",
                description = "Change the city or suburb used for the next weather check.",
            ) {
                OutlinedTextField(
                    value = state.draftLocationQuery,
                    onValueChange = viewModel::onDraftLocationChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(locationFocusRequester),
                    label = { Text("City or suburb") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        if (state.draftLocationQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onDraftLocationChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear location",
                                )
                            }
                        }
                    },
                    supportingText = {
                        Text(state.locationError ?: "Currently using ${state.preferences.locationQuery}")
                    },
                    isError = state.locationError != null,
                    singleLine = true,
                    readOnly = false,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.applyLocation() },
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("Townsville", "Cairns", "Brisbane").forEach { location ->
                        AssistChip(
                            onClick = {
                                viewModel.useSuggestedLocation(location)
                                locationFocusRequester.requestFocus()
                                keyboardController?.show()
                            },
                            label = { Text(location) },
                        )
                    }
                }
                Button(
                    onClick = {
                        viewModel.applyLocation()
                        locationFocusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    enabled = state.canApplyLocation,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Apply location")
                }
            }
        }

        item {
            SettingsSection(
                title = "Temperature",
                description = "Pick the unit shown in the main recommendation and metrics.",
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TemperatureUnit.entries.forEachIndexed { index, unit ->
                        SegmentedButton(
                            selected = state.preferences.temperatureUnit == unit,
                            onClick = { viewModel.updateTemperatureUnit(unit) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = TemperatureUnit.entries.size,
                            ),
                            label = { Text(if (unit == TemperatureUnit.Celsius) "Celsius" else "Fahrenheit") },
                        )
                    }
                }
            }
        }

        item {
            SettingsSection(
                title = "Advice sensitivity",
                description = sensitivityDescription(state.preferences.sensitivity),
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AdviceSensitivity.entries.forEachIndexed { index, sensitivity ->
                        SegmentedButton(
                            selected = state.preferences.sensitivity == sensitivity,
                            onClick = { viewModel.updateSensitivity(sensitivity) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = AdviceSensitivity.entries.size,
                            ),
                            label = { Text(sensitivity.name) },
                        )
                    }
                }
            }
        }

        item {
            SettingsSection(
                title = "Visible metrics",
                description = "Choose how much detail stays on the Ready screen.",
            ) {
                ToggleRow(
                    icon = Icons.Filled.WbSunny,
                    label = "Show UV index",
                    supportingText = "Keep sun exposure visible in the conditions grid.",
                    checked = state.preferences.showUv,
                    onCheckedChange = viewModel::updateShowUv,
                )
                ToggleRow(
                    icon = Icons.Filled.Air,
                    label = "Show wind speed",
                    supportingText = "Show breeze and gust levels in the conditions grid.",
                    checked = state.preferences.showWind,
                    onCheckedChange = viewModel::updateShowWind,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}

private fun sensitivityDescription(sensitivity: AdviceSensitivity): String =
    when (sensitivity) {
        AdviceSensitivity.Low -> "Low keeps warnings later and avoids over-alerting."
        AdviceSensitivity.Normal -> "Normal balances early warnings with everyday practicality."
        AdviceSensitivity.High -> "High gives earlier warnings when rain, heat, UV, or wind rise."
    }
