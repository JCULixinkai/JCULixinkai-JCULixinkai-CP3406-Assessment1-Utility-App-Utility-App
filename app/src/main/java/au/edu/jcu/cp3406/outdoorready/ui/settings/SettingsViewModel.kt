package au.edu.jcu.cp3406.outdoorready.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406.outdoorready.data.repository.SettingsRepository
import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.SettingsUiState
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val draftLocationQuery = MutableStateFlow(settingsRepository.preferences.value.locationQuery)
    private val locationError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.preferences,
        draftLocationQuery,
        locationError,
    ) { preferences, draftLocation, error ->
        SettingsUiState(
            preferences = preferences,
            draftLocationQuery = draftLocation,
            locationError = error,
            canApplyLocation = draftLocation.trim().isNotBlank() &&
                draftLocation.trim() != preferences.locationQuery,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsUiState(),
    )

    fun onDraftLocationChange(value: String) {
        draftLocationQuery.value = value
        locationError.value = null
    }

    fun applyLocation() {
        val trimmedValue = draftLocationQuery.value.trim()
        if (trimmedValue.isBlank()) {
            locationError.value = "Enter a city or suburb before applying."
            return
        }
        settingsRepository.updateLocationQuery(trimmedValue)
        draftLocationQuery.value = trimmedValue
        locationError.value = null
    }

    fun useSuggestedLocation(locationQuery: String) {
        val trimmedValue = locationQuery.trim()
        draftLocationQuery.value = trimmedValue
        settingsRepository.updateLocationQuery(trimmedValue)
        locationError.value = null
    }

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        settingsRepository.updateTemperatureUnit(unit)
    }

    fun updateSensitivity(sensitivity: AdviceSensitivity) {
        settingsRepository.updateSensitivity(sensitivity)
    }

    fun updateShowUv(showUv: Boolean) {
        settingsRepository.updateShowUv(showUv)
    }

    fun updateShowWind(showWind: Boolean) {
        settingsRepository.updateShowWind(showWind)
    }
}
