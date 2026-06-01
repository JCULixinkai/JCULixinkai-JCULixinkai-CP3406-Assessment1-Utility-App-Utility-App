package au.edu.jcu.cp3406.outdoorready.domain

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.ReadinessSummary
import au.edu.jcu.cp3406.outdoorready.model.SummarySeverity
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
                ),
                severity = SummarySeverity.High,
            )

            snapshot.uvIndex >= thresholds.uvIndex -> ReadinessSummary(
                headline = "Sun Protection Needed",
                reason = "UV index is ${snapshot.uvIndex.format(1)}, which is above your comfort threshold.",
                advice = listOf(
                    "Use sunscreen before you leave.",
                    "Bring sunglasses or a hat.",
                ),
                severity = SummarySeverity.High,
            )

            snapshot.windSpeedKph >= thresholds.windSpeed -> ReadinessSummary(
                headline = "Windy Conditions",
                reason = "Wind speed is ${snapshot.windSpeedKph.format(0)} km/h right now.",
                advice = listOf(
                    "Secure loose items before heading out.",
                    "Expect stronger gusts in open areas.",
                ),
                severity = SummarySeverity.Moderate,
            )

            snapshot.feelsLikeCelsius >= thresholds.feelsLikeCelsius -> ReadinessSummary(
                headline = "Warm Conditions",
                reason = "It feels like ${snapshot.feelsLikeCelsius.format(0)} C outside.",
                advice = listOf(
                    "Bring water if you will be outside for a while.",
                    "Light clothing will feel more comfortable.",
                ),
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

    private fun Double.format(decimals: Int): String =
        "%.${decimals}f".format(this)
}

