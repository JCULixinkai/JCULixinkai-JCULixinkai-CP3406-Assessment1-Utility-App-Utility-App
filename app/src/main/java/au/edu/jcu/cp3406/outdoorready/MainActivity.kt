package au.edu.jcu.cp3406.outdoorready

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import au.edu.jcu.cp3406.outdoorready.ui.OutdoorReadyApp
import au.edu.jcu.cp3406.outdoorready.ui.settings.SettingsViewModel
import au.edu.jcu.cp3406.outdoorready.ui.theme.OutdoorReadyTheme
import au.edu.jcu.cp3406.outdoorready.ui.utility.UtilityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val utilityViewModel: UtilityViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OutdoorReadyTheme {
                OutdoorReadyApp(
                    utilityViewModel = utilityViewModel,
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }
}

