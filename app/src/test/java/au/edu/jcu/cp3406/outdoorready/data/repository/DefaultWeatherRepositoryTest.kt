package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.data.remote.OpenMeteoApiService
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.ForecastResponseDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.GeocodingResponseDto
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultWeatherRepositoryTest {
    @Test
    fun `returns offline fallback when weather service is unreachable`() = runTest {
        val repository = DefaultWeatherRepository(FailingOpenMeteoApiService())

        val result = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))

        assertTrue(result is WeatherLoadResult.Success)
        val snapshot = (result as WeatherLoadResult.Success).snapshot
        assertEquals("Townsville", snapshot.locationName)
        assertEquals("Offline estimate", snapshot.conditionLabel)
        assertTrue(snapshot.isFallback)
    }

    private class FailingOpenMeteoApiService : OpenMeteoApiService {
        override suspend fun searchLocation(
            url: String,
            name: String,
            count: Int,
            language: String,
            format: String,
        ): GeocodingResponseDto = throw IOException("No route to host")

        override suspend fun getForecast(
            latitude: Double,
            longitude: Double,
            current: String,
            hourly: String,
            forecastDays: Int,
            timezone: String,
        ): ForecastResponseDto = throw IOException("No route to host")
    }
}
