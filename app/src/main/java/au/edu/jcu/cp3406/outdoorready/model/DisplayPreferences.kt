package au.edu.jcu.cp3406.outdoorready.model

data class DisplayPreferences(
    val locationQuery: String = "Townsville",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    val sensitivity: AdviceSensitivity = AdviceSensitivity.Normal,
    val showUv: Boolean = true,
    val showWind: Boolean = true,
)

enum class TemperatureUnit {
    Celsius,
    Fahrenheit,
}

enum class AdviceSensitivity {
    Low,
    Normal,
    High,
}

