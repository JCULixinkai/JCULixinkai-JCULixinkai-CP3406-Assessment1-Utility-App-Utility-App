package au.edu.jcu.cp3406.outdoorready.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import au.edu.jcu.cp3406.outdoorready.ui.settings.SettingsScreen
import au.edu.jcu.cp3406.outdoorready.ui.settings.SettingsViewModel
import au.edu.jcu.cp3406.outdoorready.ui.utility.UtilityScreen
import au.edu.jcu.cp3406.outdoorready.ui.utility.UtilityViewModel

@Composable
fun OutdoorReadyApp(
    utilityViewModel: UtilityViewModel,
    settingsViewModel: SettingsViewModel,
) {
    var currentTab by rememberSaveable { mutableStateOf(AppTab.Ready) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.Ready,
                    onClick = { currentTab = AppTab.Ready },
                    icon = { Icon(Icons.Filled.WbSunny, contentDescription = AppTab.Ready.label) },
                    label = { androidx.compose.material3.Text(AppTab.Ready.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.Settings,
                    onClick = { currentTab = AppTab.Settings },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = AppTab.Settings.label) },
                    label = { androidx.compose.material3.Text(AppTab.Settings.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            }
        },
    ) { innerPadding ->
        when (currentTab) {
            AppTab.Ready -> UtilityScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = utilityViewModel,
                onOpenSettings = { currentTab = AppTab.Settings },
            )

            AppTab.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = settingsViewModel,
            )
        }
    }
}
