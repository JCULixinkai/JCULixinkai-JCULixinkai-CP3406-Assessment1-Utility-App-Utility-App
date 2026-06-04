package au.edu.jcu.cp3406.outdoorready.ui.utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import au.edu.jcu.cp3406.outdoorready.model.UtilityUiState
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import au.edu.jcu.cp3406.outdoorready.ui.components.AdviceCard
import au.edu.jcu.cp3406.outdoorready.ui.components.ErrorPane
import au.edu.jcu.cp3406.outdoorready.ui.components.LastUpdatedRow
import au.edu.jcu.cp3406.outdoorready.ui.components.LoadingPane
import au.edu.jcu.cp3406.outdoorready.ui.components.MetricCard
import au.edu.jcu.cp3406.outdoorready.ui.components.ScreenHeader
import au.edu.jcu.cp3406.outdoorready.ui.components.SectionHeading
import au.edu.jcu.cp3406.outdoorready.ui.components.StatusHeroCard
import java.time.Duration
import java.time.Instant
import kotlin.math.roundToInt

@Composable
fun UtilityScreen(
    viewModel: UtilityViewModel,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            UtilityHeader(
                subtitle = when (state) {
                    is UtilityUiState.Success -> state.snapshot.locationName
                    else -> "Live weather-based advice before you head out"
                },
                onRefresh = viewModel::refresh,
            )
        }

        when (state) {
            UtilityUiState.Loading -> {
                item { LoadingPane() }
            }

            is UtilityUiState.Error -> {
                item {
                    ErrorPane(
                        title = "Couldn't load conditions",
                        message = state.message,
                        actionLabel = "Try again",
                        onAction = viewModel::refresh,
                    )
                }
            }

            is UtilityUiState.InvalidLocation -> {
                item {
                    ErrorPane(
                        title = "Location not found",
                        message = state.message,
                        actionLabel = "Edit in Settings",
                        onAction = onOpenSettings,
                    )
                }
            }

            is UtilityUiState.Success -> {
                item {
                    StatusHeroCard(
                        summary = state.summary,
                        conditionLabel = state.snapshot.conditionLabel,
                        locationLabel = state.snapshot.locationName,
                        temperatureLabel = formatTemperature(
                            temperatureCelsius = state.snapshot.temperatureCelsius,
                            unit = state.preferences.temperatureUnit,
                        ),
                        rainLabel = "${state.snapshot.rainChance}%",
                        feelsLikeLabel = formatTemperature(
                            temperatureCelsius = state.snapshot.feelsLikeCelsius,
                            unit = state.preferences.temperatureUnit,
                        ),
                    )
                }
                if (state.snapshot.isFallback) {
                    item {
                        Text(
                            text = "Using an offline estimate because live weather did not respond. Refresh when the connection is stable.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    SectionHeading(
                        title = "Current conditions",
                        subtitle = "The details driving the recommendation right now",
                    )
                }
                item {
                    MetricSection(
                        snapshot = state.snapshot,
                        preferences = state.preferences,
                    )
                }
                item {
                    AdviceCard(items = state.summary.advice)
                }
                item {
                    LastUpdatedRow(
                        label = formatRelativeUpdate(state.snapshot.fetchedAt),
                        onRefresh = viewModel::refresh,
                    )
                }
            }
        }

        if (state !is UtilityUiState.Success) {
            item {
                Text(
                    text = "Use Settings to change city, display units, and how cautious the advice should be.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun UtilityHeader(
    subtitle: String,
    onRefresh: () -> Unit,
) {
    ScreenHeader(
        title = "Outdoor Ready",
        subtitle = subtitle,
        actionIcon = Icons.Filled.Refresh,
        actionDescription = "Refresh",
        onAction = onRefresh,
    )
}

@Composable
private fun MetricSection(
    snapshot: WeatherSnapshot,
    preferences: DisplayPreferences,
) {
    val metrics = buildList {
        add(
            MetricData(
                title = "Temperature",
                value = formatTemperature(snapshot.temperatureCelsius, preferences.temperatureUnit),
                supportingText = "Feels like ${formatTemperature(snapshot.feelsLikeCelsius, preferences.temperatureUnit)}",
                icon = Icons.Filled.DeviceThermostat,
                accentColor = Color(0xFFE07A15),
            ),
        )
        add(
            MetricData(
                title = "Rain chance",
                value = "${snapshot.rainChance}%",
                supportingText = rainLabel(snapshot.rainChance),
                icon = Icons.Filled.Umbrella,
                accentColor = Color(0xFF1976D2),
            ),
        )
        if (preferences.showUv) {
            add(
                MetricData(
                    title = "UV index",
                    value = snapshot.uvIndex.format(1),
                    supportingText = uvLabel(snapshot.uvIndex),
                    icon = Icons.Filled.WbSunny,
                    accentColor = Color(0xFFB26A00),
                ),
            )
        }
        if (preferences.showWind) {
            add(
                MetricData(
                    title = "Wind",
                    value = "${snapshot.windSpeedKph.roundToInt()} km/h",
                    supportingText = windLabel(snapshot.windSpeedKph),
                    icon = Icons.Filled.Air,
                    accentColor = Color(0xFF2E7D6B),
                ),
            )
        }
    }

    BoxWithConstraints {
        val useTwoColumns = maxWidth >= 360.dp
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            metrics.chunked(if (useTwoColumns) 2 else 1).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { metric ->
                        MetricCard(
                            title = metric.title,
                            value = metric.value,
                            supportingText = metric.supportingText,
                            icon = metric.icon,
                            accentColor = metric.accentColor,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (useTwoColumns && rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class MetricData(
    val title: String,
    val value: String,
    val supportingText: String,
    val icon: ImageVector,
    val accentColor: Color,
)

private fun formatRelativeUpdate(fetchedAt: Instant): String {
    val minutes = Duration.between(fetchedAt, Instant.now()).toMinutes()
    return when {
        minutes <= 0 -> "Updated just now"
        minutes == 1L -> "Updated 1 minute ago"
        else -> "Updated $minutes minutes ago"
    }
}

private fun formatTemperature(
    temperatureCelsius: Double,
    unit: TemperatureUnit,
): String =
    when (unit) {
        TemperatureUnit.Celsius -> "${temperatureCelsius.roundToInt()} C"
        TemperatureUnit.Fahrenheit -> "${(temperatureCelsius * 9 / 5 + 32).roundToInt()} F"
    }

private fun uvLabel(uvIndex: Double): String =
    when {
        uvIndex >= 8 -> "High exposure"
        uvIndex >= 6 -> "Moderate exposure"
        else -> "Low exposure"
    }

private fun windLabel(windSpeed: Double): String =
    when {
        windSpeed >= 35 -> "Strong gusts"
        windSpeed >= 20 -> "Moderate gusts"
        else -> "Light breeze"
    }

private fun rainLabel(rainChance: Int): String =
    when {
        rainChance >= 70 -> "Very likely soon"
        rainChance >= 40 -> "Worth planning for"
        else -> "Low chance right now"
    }

private fun Double.format(decimals: Int): String =
    "%.${decimals}f".format(this)
