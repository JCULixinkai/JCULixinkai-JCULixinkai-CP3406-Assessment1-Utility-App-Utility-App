package au.edu.jcu.cp3406.outdoorready.domain

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
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

    private fun sampleSnapshot(
        rainChance: Int = 20,
        uvIndex: Double = 3.0,
        windSpeedKph: Double = 12.0,
    ) = WeatherSnapshot(
        locationName = "Townsville",
        conditionLabel = "Clear",
        temperatureCelsius = 26.0,
        feelsLikeCelsius = 27.0,
        rainChance = rainChance,
        uvIndex = uvIndex,
        windSpeedKph = windSpeedKph,
        fetchedAt = Instant.now(),
    )
}

