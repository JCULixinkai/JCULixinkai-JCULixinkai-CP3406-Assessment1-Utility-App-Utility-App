package au.edu.jcu.cp3406.outdoorready.model

data class SettingsUiState(
    val preferences: DisplayPreferences = DisplayPreferences(),
    val draftLocationQuery: String = "",
    val locationError: String? = null,
    val canApplyLocation: Boolean = false,
)

