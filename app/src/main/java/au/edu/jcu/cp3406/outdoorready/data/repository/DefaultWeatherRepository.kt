package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.data.remote.OpenMeteoApiService
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.LocationResultDto
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import retrofit2.HttpException

class DefaultWeatherRepository @Inject constructor(
    private val apiService: OpenMeteoApiService,
) : WeatherRepository {
    override suspend fun loadWeather(preferences: DisplayPreferences): WeatherLoadResult {
        val locationQuery = preferences.locationQuery.trim()
        if (locationQuery.isBlank()) {
            return WeatherLoadResult.LocationNotFound(locationQuery)
        }

        return try {
            val location = apiService.searchLocation(name = locationQuery).results?.firstOrNull()
                ?: return WeatherLoadResult.LocationNotFound(locationQuery)

            val forecast = apiService.getForecast(
                latitude = location.latitude,
                longitude = location.longitude,
            )

            val current = forecast.current
                ?: return WeatherLoadResult.Failure("Current conditions are unavailable right now.")

            val currentTime = current.time
            val hourly = forecast.hourly
            val hourlyIndex = if (currentTime == null || hourly == null) {
                -1
            } else {
                hourly.time.indexOf(currentTime)
            }

            val rainChance = hourly?.precipitationProbability
                ?.getOrNull(hourlyIndex.takeIf { it >= 0 } ?: 0)
                ?: 0
            val uvIndex = hourly?.uvIndex
                ?.getOrNull(hourlyIndex.takeIf { it >= 0 } ?: 0)
                ?: 0.0

            val snapshot = WeatherSnapshot(
                locationName = location.displayName(),
                conditionLabel = conditionLabelFor(current.weatherCode),
                temperatureCelsius = current.temperature2m ?: 0.0,
                feelsLikeCelsius = current.apparentTemperature ?: current.temperature2m ?: 0.0,
                rainChance = rainChance,
                uvIndex = uvIndex,
                windSpeedKph = current.windSpeed10m ?: 0.0,
                fetchedAt = Instant.now(),
            )

            WeatherLoadResult.Success(snapshot)
        } catch (_: IOException) {
            WeatherLoadResult.Success(offlineSnapshot(locationQuery))
        } catch (exception: HttpException) {
            WeatherLoadResult.Failure(
                "Weather service returned error ${exception.code()}. Try refreshing in a moment.",
            )
        } catch (_: JsonDataException) {
            WeatherLoadResult.Failure("Weather data changed format. Try refreshing in a moment.")
        } catch (_: JsonEncodingException) {
            WeatherLoadResult.Failure("Weather data could not be read. Try refreshing in a moment.")
        } catch (_: Exception) {
            WeatherLoadResult.Failure("Couldn't load the latest conditions. Try refreshing in a moment.")
        }
    }

    private fun offlineSnapshot(locationQuery: String): WeatherSnapshot =
        WeatherSnapshot(
            locationName = locationQuery.ifBlank { "Townsville" },
            conditionLabel = "Offline estimate",
            temperatureCelsius = 26.0,
            feelsLikeCelsius = 27.0,
            rainChance = 20,
            uvIndex = 4.0,
            windSpeedKph = 12.0,
            fetchedAt = Instant.now(),
            isFallback = true,
        )

    private fun LocationResultDto.displayName(): String =
        listOfNotNull(name, admin1).distinct().joinToString(", ")

    private fun conditionLabelFor(weatherCode: Int?): String =
        when (weatherCode) {
            0 -> "Clear"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55, 56, 57 -> "Drizzle"
            61, 63, 65, 80, 81, 82 -> "Rain"
            66, 67 -> "Freezing rain"
            71, 73, 75, 77, 85, 86 -> "Snow"
            95, 96, 99 -> "Storm"
            else -> "Unsettled"
        }
}
