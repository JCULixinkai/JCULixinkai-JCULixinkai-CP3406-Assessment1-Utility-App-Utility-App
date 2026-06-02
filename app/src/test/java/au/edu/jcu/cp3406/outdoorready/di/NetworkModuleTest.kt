package au.edu.jcu.cp3406.outdoorready.di

import au.edu.jcu.cp3406.outdoorready.data.remote.dto.ForecastResponseDto
import au.edu.jcu.cp3406.outdoorready.data.remote.dto.GeocodingResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NetworkModuleTest {
    @Test
    fun `moshi parses geocoding response into kotlin dto`() {
        val json = """
            {
              "results": [
                {
                  "id": 2146142,
                  "name": "Townsville",
                  "latitude": -19.26639,
                  "longitude": 146.8057,
                  "country": "Australia",
                  "admin1": "Queensland"
                }
              ]
            }
        """.trimIndent()

        val adapter = NetworkModule.provideMoshi().adapter(GeocodingResponseDto::class.java)
        val result = adapter.fromJson(json)

        assertNotNull(result)
        assertEquals("Townsville", result?.results?.firstOrNull()?.name)
        assertEquals(-19.26639, result?.results?.firstOrNull()?.latitude ?: 0.0, 0.0)
    }

    @Test
    fun `moshi parses forecast response into kotlin dto`() {
        val json = """
            {
              "current": {
                "time": "2026-05-30T01:00",
                "temperature_2m": 18.0,
                "apparent_temperature": 17.8,
                "weather_code": 0,
                "wind_speed_10m": 6.8
              },
              "hourly": {
                "time": ["2026-05-30T00:00", "2026-05-30T01:00"],
                "precipitation_probability": [0, 10],
                "uv_index": [0.0, 0.65]
              }
            }
        """.trimIndent()

        val adapter = NetworkModule.provideMoshi().adapter(ForecastResponseDto::class.java)
        val result = adapter.fromJson(json)

        assertNotNull(result)
        assertEquals(18.0, result?.current?.temperature2m ?: 0.0, 0.0)
        assertEquals(10, result?.hourly?.precipitationProbability?.getOrNull(1))
        assertEquals(0.65, result?.hourly?.uvIndex?.getOrNull(1) ?: 0.0, 0.0)
    }
}
