package au.edu.jcu.cp3406.outdoorready.data.remote.dto

import com.squareup.moshi.Json

data class ForecastResponseDto(
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null,
)

data class CurrentWeatherDto(
    val time: String? = null,
    @param:Json(name = "temperature_2m")
    val temperature2m: Double? = null,
    @param:Json(name = "apparent_temperature")
    val apparentTemperature: Double? = null,
    @param:Json(name = "weather_code")
    val weatherCode: Int? = null,
    @param:Json(name = "wind_speed_10m")
    val windSpeed10m: Double? = null,
)

data class HourlyWeatherDto(
    val time: List<String> = emptyList(),
    @param:Json(name = "precipitation_probability")
    val precipitationProbability: List<Int?> = emptyList(),
    @param:Json(name = "uv_index")
    val uvIndex: List<Double?> = emptyList(),
)
