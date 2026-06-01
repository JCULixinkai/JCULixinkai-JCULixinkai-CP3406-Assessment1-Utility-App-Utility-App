package au.edu.jcu.cp3406.outdoorready.data.remote

import au.edu.jcu.cp3406.outdoorready.data.remote.dto.ForecastResponseDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface OpenMeteoApiService {
    @GET
    suspend fun searchLocation(
        @Url url: String = "https://geocoding-api.open-meteo.com/v1/search",
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json",
    ): GeocodingResponseDto

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "precipitation_probability,uv_index",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("timezone") timezone: String = "auto",
    ): ForecastResponseDto
}

