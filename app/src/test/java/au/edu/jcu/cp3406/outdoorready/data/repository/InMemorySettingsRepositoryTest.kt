package au.edu.jcu.cp3406.outdoorready.data.repository

import au.edu.jcu.cp3406.outdoorready.model.AdviceSensitivity
import au.edu.jcu.cp3406.outdoorready.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemorySettingsRepositoryTest {
    private val repository = InMemorySettingsRepository()

    @Test
    fun `updates location and display preferences in memory`() {
        repository.updateLocationQuery("Cairns")
        repository.updateTemperatureUnit(TemperatureUnit.Fahrenheit)
        repository.updateSensitivity(AdviceSensitivity.High)
        repository.updateShowUv(false)

        val preferences = repository.preferences.value

        assertEquals("Cairns", preferences.locationQuery)
        assertEquals(TemperatureUnit.Fahrenheit, preferences.temperatureUnit)
        assertEquals(AdviceSensitivity.High, preferences.sensitivity)
        assertFalse(preferences.showUv)
        assertTrue(preferences.showWind)
    }
}

