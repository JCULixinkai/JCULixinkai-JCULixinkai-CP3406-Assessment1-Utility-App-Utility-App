package au.edu.jcu.cp3406.outdoorready.ui.utility

import au.edu.jcu.cp3406.outdoorready.MainDispatcherRule
import au.edu.jcu.cp3406.outdoorready.data.repository.SettingsRepository
import au.edu.jcu.cp3406.outdoorready.data.repository.WeatherLoadResult
import au.edu.jcu.cp3406.outdoorready.data.repository.WeatherRepository
import au.edu.jcu.cp3406.outdoorready.domain.ReadinessEvaluator
import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import au.edu.jcu.cp3406.outdoorready.model.UtilityUiState
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UtilityViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads success on startup`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val weatherRepository = FakeWeatherRepository(WeatherLoadResult.Success(sampleSnapshot()))

        val viewModel = UtilityViewModel(
            weatherRepository = weatherRepository,
            settingsRepository = settingsRepository,
            readinessEvaluator = ReadinessEvaluator(),
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UtilityUiState.Success)
    }

    @Test
    fun `emits invalid location when repository cannot geocode`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val weatherRepository = FakeWeatherRepository(WeatherLoadResult.LocationNotFound("X"))

        val viewModel = UtilityViewModel(
            weatherRepository = weatherRepository,
            settingsRepository = settingsRepository,
            readinessEvaluator = ReadinessEvaluator(),
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UtilityUiState.InvalidLocation)
    }

    private class FakeWeatherRepository(
        private val result: WeatherLoadResult,
    ) : WeatherRepository {
        override suspend fun loadWeather(preferences: DisplayPreferences): WeatherLoadResult = result
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val mutablePreferences = MutableStateFlow(DisplayPreferences())

        override val preferences: StateFlow<DisplayPreferences> = mutablePreferences

        override fun updateLocationQuery(locationQuery: String) {
            mutablePreferences.value = mutablePreferences.value.copy(locationQuery = locationQuery)
        }

        override fun updateTemperatureUnit(unit: TemperatureUnit) {
            mutablePreferences.value = mutablePreferences.value.copy(temperatureUnit = unit)
        }

        override fun updateSensitivity(sensitivity: AdviceSensitivity) {
            mutablePreferences.value = mutablePreferences.value.copy(sensitivity = sensitivity)
        }

        override fun updateShowUv(showUv: Boolean) {
            mutablePreferences.value = mutablePreferences.value.copy(showUv = showUv)
        }

        override fun updateShowWind(showWind: Boolean) {
            mutablePreferences.value = mutablePreferences.value.copy(showWind = showWind)
        }
    }

    private fun sampleSnapshot() = WeatherSnapshot(
        locationName = "Townsville",
        conditionLabel = "Clear",
        temperatureCelsius = 25.0,
        feelsLikeCelsius = 26.0,
        rainChance = 20,
        uvIndex = 4.0,
        windSpeedKph = 12.0,
        fetchedAt = Instant.now(),
    )
}
