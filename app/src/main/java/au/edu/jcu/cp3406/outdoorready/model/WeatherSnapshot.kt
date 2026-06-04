package au.edu.jcu.cp3406.outdoorready.model

import java.time.Instant

data class WeatherSnapshot(
    val locationName: String,
    val conditionLabel: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val rainChance: Int,
    val uvIndex: Double,
    val windSpeedKph: Double,
    val fetchedAt: Instant,
    val isFallback: Boolean = false,
)
