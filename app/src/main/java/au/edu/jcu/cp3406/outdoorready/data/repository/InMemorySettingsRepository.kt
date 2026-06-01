package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySettingsRepository @Inject constructor() : SettingsRepository {
    private val mutablePreferences = MutableStateFlow(DisplayPreferences())

    override val preferences: StateFlow<DisplayPreferences> = mutablePreferences.asStateFlow()

    override fun updateLocationQuery(locationQuery: String) {
        mutablePreferences.update { it.copy(locationQuery = locationQuery.trim()) }
    }

    override fun updateTemperatureUnit(unit: TemperatureUnit) {
        mutablePreferences.update { it.copy(temperatureUnit = unit) }
    }

    override fun updateSensitivity(sensitivity: AdviceSensitivity) {
        mutablePreferences.update { it.copy(sensitivity = sensitivity) }
    }

    override fun updateShowUv(showUv: Boolean) {
        mutablePreferences.update { it.copy(showUv = showUv) }
    }

    override fun updateShowWind(showWind: Boolean) {
        mutablePreferences.update { it.copy(showWind = showWind) }
    }
}

