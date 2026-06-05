package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.data.remote.OpenMeteoApiService
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.CurrentWeatherDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.ForecastResponseDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.GeocodingResponseDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.HourlyWeatherDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.LocationResultDto
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultWeatherRepositoryTest {
    @Test
    fun `returns failure when weather service is unreachable before any live result`() = runTest {
        val repository = DefaultWeatherRepository(FailingOpenMeteoApiService())

        val result = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))

        assertTrue(result is WeatherLoadResult.Failure)
        assertEquals(
            "Live weather is unavailable. Check your connection and try again.",
            (result as WeatherLoadResult.Failure).message,
        )
    }

    @Test
    fun `maps successful api response into weather snapshot`() = runTest {
        val repository = DefaultWeatherRepository(SuccessfulOpenMeteoApiService())

        val result = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))

        assertTrue(result is WeatherLoadResult.Success)
        val snapshot = (result as WeatherLoadResult.Success).snapshot
        assertEquals("Townsville, Queensland", snapshot.locationName)
        assertEquals("Rain", snapshot.conditionLabel)
        assertEquals(24.4, snapshot.temperatureCelsius, 0.0)
        assertEquals(25.1, snapshot.feelsLikeCelsius, 0.0)
        assertEquals(65, snapshot.rainChance)
        assertEquals(7.3, snapshot.uvIndex, 0.0)
        assertEquals(18.0, snapshot.windSpeedKph, 0.0)
        assertFalse(snapshot.isFallback)
    }

    @Test
    fun `returns cached snapshot when same location cannot refresh`() = runTest {
        val apiService = FlakyOpenMeteoApiService()
        val repository = DefaultWeatherRepository(apiService)

        val liveResult = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))
        apiService.failNextRequest = true
        val cachedResult = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))

        assertTrue(liveResult is WeatherLoadResult.Success)
        assertTrue(cachedResult is WeatherLoadResult.Success)
        val cachedSnapshot = (cachedResult as WeatherLoadResult.Success).snapshot
        assertEquals("Townsville, Queensland", cachedSnapshot.locationName)
        assertEquals("Rain", cachedSnapshot.conditionLabel)
        assertTrue(cachedSnapshot.isFallback)
    }

    @Test
    fun `missing hourly data falls back to zero rain and uv`() = runTest {
        val repository = DefaultWeatherRepository(
            SuccessfulOpenMeteoApiService(
                forecastResponse = sampleForecast(hourly = null),
            ),
        )

        val result = repository.loadWeather(DisplayPreferences(locationQuery = "Townsville"))

        assertTrue(result is WeatherLoadResult.Success)
        val snapshot = (result as WeatherLoadResult.Success).snapshot
        assertEquals(0, snapshot.rainChance)
        assertEquals(0.0, snapshot.uvIndex, 0.0)
    }

    private open class SuccessfulOpenMeteoApiService(
        private val forecastResponse: ForecastResponseDto = sampleForecast(),
    ) : OpenMeteoApiService {
        override suspend fun searchLocation(
            url: String,
            name: String,
            count: Int,
            language: String,
            format: String,
        ): GeocodingResponseDto =
            GeocodingResponseDto(
                results = listOf(
                    LocationResultDto(
                        name = "Townsville",
                        admin1 = "Queensland",
                        country = "Australia",
                        latitude = -19.26639,
                        longitude = 146.8057,
                    ),
                ),
            )

        override suspend fun getForecast(
            latitude: Double,
            longitude: Double,
            current: String,
            hourly: String,
            forecastDays: Int,
            timezone: String,
        ): ForecastResponseDto = forecastResponse
    }

    private class FlakyOpenMeteoApiService : SuccessfulOpenMeteoApiService() {
        var failNextRequest = false

        override suspend fun searchLocation(
            url: String,
            name: String,
            count: Int,
            language: String,
            format: String,
        ): GeocodingResponseDto {
            if (failNextRequest) {
                throw IOException("No route to host")
            }
            return super.searchLocation(url, name, count, language, format)
        }
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

    private companion object {
        fun sampleForecast(hourly: HourlyWeatherDto? = sampleHourly()) =
            ForecastResponseDto(
                current = CurrentWeatherDto(
                    time = "2026-06-05T10:00",
                    temperature2m = 24.4,
                    apparentTemperature = 25.1,
                    weatherCode = 61,
                    windSpeed10m = 18.0,
                ),
                hourly = hourly,
            )

        fun sampleHourly() =
            HourlyWeatherDto(
                time = listOf("2026-06-05T09:00", "2026-06-05T10:00"),
                precipitationProbability = listOf(20, 65),
                uvIndex = listOf(3.1, 7.3),
            )
    }
}
