package au.edu.jcu.cp3406.outdoorready.domain

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadinessEvaluatorTest {
    private val evaluator = ReadinessEvaluator()

    @Test
    fun `returns umbrella summary when rain is high`() {
        val summary = evaluator.evaluate(
            snapshot = sampleSnapshot(rainChance = 80),
            preferences = DisplayPreferences(),
        )

        assertEquals("Take Umbrella", summary.headline)
    }

    @Test
    fun `returns sun protection summary when uv is high`() {
        val summary = evaluator.evaluate(
            snapshot = sampleSnapshot(rainChance = 10, uvIndex = 9.2),
            preferences = DisplayPreferences(),
        )

        assertEquals("Sun Protection Needed", summary.headline)
    }

    @Test
    fun `high sensitivity lowers thresholds`() {
        val summary = evaluator.evaluate(
            snapshot = sampleSnapshot(rainChance = 45, uvIndex = 4.5, windSpeedKph = 18.0),
            preferences = DisplayPreferences(sensitivity = AdviceSensitivity.High),
        )

        assertEquals("Take Umbrella", summary.headline)
    }

    @Test
    fun `formats warm condition reason using selected fahrenheit unit`() {
        val summary = evaluator.evaluate(
            snapshot = sampleSnapshot(
                rainChance = 10,
                uvIndex = 3.0,
                windSpeedKph = 10.0,
                feelsLikeCelsius = 32.0,
            ),
            preferences = DisplayPreferences(temperatureUnit = TemperatureUnit.Fahrenheit),
        )

        assertEquals("Warm Conditions", summary.headline)
        assertEquals("It feels like 90\u00B0F outside.", summary.reason)
    }

    @Test
    fun `adds secondary advice when more than one condition is risky`() {
        val summary = evaluator.evaluate(
            snapshot = sampleSnapshot(
                rainChance = 80,
                uvIndex = 8.0,
                windSpeedKph = 30.0,
            ),
            preferences = DisplayPreferences(),
        )

        assertEquals("Take Umbrella", summary.headline)
        assertEquals(
            listOf(
                "Carry an umbrella or rain jacket.",
                "Allow extra time if you are commuting.",
                "UV is also high, so keep sunscreen or a hat ready.",
                "Wind is also strong, so secure loose items before leaving.",
            ),
            summary.advice,
        )
    }

    private fun sampleSnapshot(
        rainChance: Int = 20,
        uvIndex: Double = 3.0,
        windSpeedKph: Double = 12.0,
        feelsLikeCelsius: Double = 27.0,
    ) = WeatherSnapshot(
        locationName = "Townsville",
        conditionLabel = "Clear",
        temperatureCelsius = 26.0,
        feelsLikeCelsius = feelsLikeCelsius,
        rainChance = rainChance,
        uvIndex = uvIndex,
        windSpeedKph = windSpeedKph,
        fetchedAt = Instant.now(),
    )
}
