package au.edu.jcu.cp3406.outdoorready.ui.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406.outdoorready.data.repository.SettingsRepository
import au.edu.jcu.cp3406.outdoorready.data.repository.WeatherLoadResult
import au.edu.jcu.cp3406.outdoorready.data.repository.WeatherRepository
import au.edu.jcu.cp3406.outdoorready.domain.ReadinessEvaluator
import au.edu.jcu.cp3406.outdoorready.model.DisplayPreferences
import au.edu.jcu.cp3406.outdoorready.model.UtilityUiState
import au.edu.jcu.cp3406.outdoorready.model.WeatherSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    private val readinessEvaluator: ReadinessEvaluator,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<UtilityUiState>(UtilityUiState.Loading)
    val uiState: StateFlow<UtilityUiState> = mutableUiState.asStateFlow()

    private var lastSnapshot: WeatherSnapshot? = null
    private var currentPreferences: DisplayPreferences? = null

    init {
        observePreferences()
    }

    fun refresh() {
        val preferences = currentPreferences ?: settingsRepository.preferences.value
        viewModelScope.launch {
            fetchWeather(preferences)
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            settingsRepository.preferences.collect { preferences ->
                val previous = currentPreferences
                currentPreferences = preferences

                if (lastSnapshot == null || previous?.locationQuery != preferences.locationQuery) {
                    fetchWeather(preferences)
                } else {
                    renderSuccess(lastSnapshot ?: return@collect, preferences)
                }
            }
        }
    }

    private suspend fun fetchWeather(preferences: DisplayPreferences) {
        mutableUiState.value = UtilityUiState.Loading
        when (val result = weatherRepository.loadWeather(preferences)) {
            is WeatherLoadResult.Success -> {
                lastSnapshot = result.snapshot
                renderSuccess(result.snapshot, preferences)
            }

            is WeatherLoadResult.LocationNotFound -> {
                mutableUiState.value = UtilityUiState.InvalidLocation(
                    message = "We couldn't find \"${result.query}\". Edit the location in Settings.",
                )
            }

            is WeatherLoadResult.Failure -> {
                mutableUiState.value = UtilityUiState.Error(result.message)
            }
        }
    }

    private fun renderSuccess(
        snapshot: WeatherSnapshot,
        preferences: DisplayPreferences,
    ) {
        mutableUiState.value = UtilityUiState.Success(
            snapshot = snapshot,
            summary = readinessEvaluator.evaluate(snapshot, preferences),
            preferences = preferences,
        )
    }
}

