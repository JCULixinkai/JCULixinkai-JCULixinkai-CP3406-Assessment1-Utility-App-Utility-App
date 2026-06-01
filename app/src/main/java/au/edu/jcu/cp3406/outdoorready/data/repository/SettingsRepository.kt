package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val preferences: StateFlow<DisplayPreferences>

    fun updateLocationQuery(locationQuery: String)

    fun updateTemperatureUnit(unit: TemperatureUnit)

    fun updateSensitivity(sensitivity: AdviceSensitivity)

    fun updateShowUv(showUv: Boolean)

    fun updateShowWind(showWind: Boolean)
}

