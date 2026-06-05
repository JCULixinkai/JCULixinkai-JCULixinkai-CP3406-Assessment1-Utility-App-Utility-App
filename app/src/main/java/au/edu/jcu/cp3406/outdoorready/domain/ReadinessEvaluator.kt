package au.edu.jcu.cp3406.outdoorready.domain

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.ReadinessSummary
import au.edu.jcu.cp3406.outdoorready.model.SummarySeverity
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import javax.inject.Inject

class ReadinessEvaluator @Inject constructor() {
    fun evaluate(
        snapshot: WeatherSnapshot,
        preferences: DisplayPreferences,
    ): ReadinessSummary {
        val thresholds = thresholdsFor(preferences.sensitivity)

        return when {
            snapshot.rainChance >= thresholds.rainChance -> ReadinessSummary(
                headline = "Take Umbrella",
                reason = "Rain chance is ${snapshot.rainChance}% for ${snapshot.locationName}.",
                advice = listOf(
                    "Carry an umbrella or rain jacket.",
                    "Allow extra time if you are commuting.",
                ) + secondaryAdvice(snapshot, thresholds, PrimaryRisk.Rain, preferences),
                severity = SummarySeverity.High,
            )

            snapshot.uvIndex >= thresholds.uvIndex -> ReadinessSummary(
                headline = "Sun Protection Needed",
                reason = "UV index is ${snapshot.uvIndex.format(1)}, which is above your comfort threshold.",
                advice = listOf(
                    "Use sunscreen before you leave.",
                    "Bring sunglasses or a hat.",
                ) + secondaryAdvice(snapshot, thresholds, PrimaryRisk.Uv, preferences),
                severity = SummarySeverity.High,
            )

            snapshot.windSpeedKph >= thresholds.windSpeed -> ReadinessSummary(
                headline = "Windy Conditions",
                reason = "Wind speed is ${snapshot.windSpeedKph.format(0)} km/h right now.",
                advice = listOf(
                    "Secure loose items before heading out.",
                    "Expect stronger gusts in open areas.",
                ) + secondaryAdvice(snapshot, thresholds, PrimaryRisk.Wind, preferences),
                severity = SummarySeverity.Moderate,
            )

            snapshot.feelsLikeCelsius >= thresholds.feelsLikeCelsius -> ReadinessSummary(
                headline = "Warm Conditions",
                reason = "It feels like ${formatTemperature(snapshot.feelsLikeCelsius, preferences.temperatureUnit)} outside.",
                advice = listOf(
                    "Bring water if you will be outside for a while.",
                    "Light clothing will feel more comfortable.",
                ) + secondaryAdvice(snapshot, thresholds, PrimaryRisk.Heat, preferences),
                severity = SummarySeverity.Moderate,
            )

            else -> ReadinessSummary(
                headline = "Good to Go",
                reason = "Conditions look manageable for a normal trip outside.",
                advice = listOf(
                    "Standard outdoor prep should be enough.",
                    "Check again later if the weather changes.",
                ),
                severity = SummarySeverity.Low,
            )
        }
    }

    private fun thresholdsFor(sensitivity: AdviceSensitivity): Thresholds =
        when (sensitivity) {
            AdviceSensitivity.Low -> Thresholds(
                rainChance = 70,
                uvIndex = 9.0,
                windSpeed = 36.0,
                feelsLikeCelsius = 34.0,
            )

            AdviceSensitivity.Normal -> Thresholds(
                rainChance = 55,
                uvIndex = 7.0,
                windSpeed = 28.0,
                feelsLikeCelsius = 31.0,
            )

            AdviceSensitivity.High -> Thresholds(
                rainChance = 40,
                uvIndex = 5.0,
                windSpeed = 22.0,
                feelsLikeCelsius = 28.0,
            )
        }

    private data class Thresholds(
        val rainChance: Int,
        val uvIndex: Double,
        val windSpeed: Double,
        val feelsLikeCelsius: Double,
    )

    private enum class PrimaryRisk {
        Rain,
        Uv,
        Wind,
        Heat,
    }

    private fun secondaryAdvice(
        snapshot: WeatherSnapshot,
        thresholds: Thresholds,
        primaryRisk: PrimaryRisk,
        preferences: DisplayPreferences,
    ): List<String> = buildList {
        if (primaryRisk != PrimaryRisk.Rain && snapshot.rainChance >= thresholds.rainChance) {
            add("Rain risk is also elevated, so keep wet weather gear nearby.")
        }
        if (primaryRisk != PrimaryRisk.Uv && snapshot.uvIndex >= thresholds.uvIndex) {
            add("UV is also high, so keep sunscreen or a hat ready.")
        }
        if (primaryRisk != PrimaryRisk.Wind && snapshot.windSpeedKph >= thresholds.windSpeed) {
            add("Wind is also strong, so secure loose items before leaving.")
        }
        if (primaryRisk != PrimaryRisk.Heat && snapshot.feelsLikeCelsius >= thresholds.feelsLikeCelsius) {
            add("It also feels like ${formatTemperature(snapshot.feelsLikeCelsius, preferences.temperatureUnit)}, so take water.")
        }
    }

    private fun formatTemperature(
        temperatureCelsius: Double,
        unit: TemperatureUnit,
    ): String =
        when (unit) {
            TemperatureUnit.Celsius -> "${temperatureCelsius.format(0)}\u00B0C"
            TemperatureUnit.Fahrenheit -> "${(temperatureCelsius * 9 / 5 + 32).format(0)}\u00B0F"
        }

    private fun Double.format(decimals: Int): String =
        "%.${decimals}f".format(this)
}
