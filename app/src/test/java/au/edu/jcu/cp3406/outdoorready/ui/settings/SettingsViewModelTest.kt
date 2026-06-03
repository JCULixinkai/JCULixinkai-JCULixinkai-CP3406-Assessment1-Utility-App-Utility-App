package au.edu.jcu.cp3406.outdoorready.ui.settings

import au.edu.jcu.cp3406.outdoorready.MainDispatcherRule
import au.edu.jcu.cp3406.outdoorready.data.repository.SettingsRepository
import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `keeps blank draft while editing location`() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.onDraftLocationChange("")
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.draftLocationQuery)
        assertFalse(viewModel.uiState.value.canApplyLocation)
    }

    @Test
    fun `applies changed location and disables apply afterwards`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onDraftLocationChange("Cairns")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canApplyLocation)

        viewModel.applyLocation()
        advanceUntilIdle()

        assertEquals("Cairns", repository.preferences.value.locationQuery)
        assertEquals("Cairns", viewModel.uiState.value.draftLocationQuery)
        assertFalse(viewModel.uiState.value.canApplyLocation)
    }

    @Test
    fun `shows inline error when blank location is applied`() = runTest {
        val viewModel = SettingsViewModel(FakeSettingsRepository())

        viewModel.onDraftLocationChange("   ")
        viewModel.applyLocation()
        advanceUntilIdle()

        assertEquals(
            "Enter a city or suburb before applying.",
            viewModel.uiState.value.locationError,
        )
    }

    @Test
    fun `suggested location applies immediately`() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.useSuggestedLocation("Brisbane")
        advanceUntilIdle()

        assertEquals("Brisbane", repository.preferences.value.locationQuery)
        assertEquals("Brisbane", viewModel.uiState.value.draftLocationQuery)
        assertFalse(viewModel.uiState.value.canApplyLocation)
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
}
