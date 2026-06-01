package au.edu.jcu.cp3406.outdoorready.data.remote.dto

data class GeocodingResponseDto(
    val results: List<LocationResultDto>? = null,
)

data class LocationResultDto(
    val name: String,
    val admin1: String? = null,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double,
)

