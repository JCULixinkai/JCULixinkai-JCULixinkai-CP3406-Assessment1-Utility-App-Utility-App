package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot

interface WeatherRepository {
    suspend fun loadWeather(preferences: DisplayPreferences): WeatherLoadResult
}

sealed interface WeatherLoadResult {
    data class Success(val snapshot: WeatherSnapshot) : WeatherLoadResult

    data class LocationNotFound(val query: String) : WeatherLoadResult

    data class Failure(val message: String) : WeatherLoadResult
}

