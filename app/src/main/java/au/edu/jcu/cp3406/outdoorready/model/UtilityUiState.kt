package au.edu.jcu.cp3406.outdoorready.model

sealed interface UtilityUiState {
    data object Loading : UtilityUiState

    data class Success(
        val snapshot: WeatherSnapshot,
        val summary: ReadinessSummary,
        val preferences: DisplayPreferences,
    ) : UtilityUiState

    data class Error(
        val message: String,
    ) : UtilityUiState

    data class InvalidLocation(
        val message: String,
    ) : UtilityUiState
}

