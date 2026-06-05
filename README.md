# Outdoor Ready

Outdoor Ready is a focused Android utility app that gives users a quick answer
before they leave home: what should I prepare for the current outdoor
conditions?

## Core Features

- Ready screen with an at-a-glance outdoor recommendation
- Live weather lookup for the selected city or suburb
- Current temperature, feels-like temperature, rain chance, UV index, and wind
  speed metrics
- Settings screen for location, temperature unit, advice sensitivity, and
  visible metrics
- Clear loading, invalid location, network error, and cached weather states

## Implementation

- Kotlin Android app using Jetpack Compose and Material 3
- Single-activity UI with a bottom navigation bar for Ready and Settings
- ViewModels expose screen state with Kotlin Flow
- Repository interfaces separate settings and weather data logic from UI code
- Hilt provides dependency injection for repositories, Retrofit, OkHttp, and
  Moshi
- Retrofit calls the Open-Meteo geocoding and forecast APIs
- Unit tests cover readiness rules, settings updates, network parsing,
  repository fallback behavior, and ViewModel state changes

## Running The App

Open the project in Android Studio, select an emulator or Android device, and
run the app module.

From the terminal:

```bash
./gradlew assembleDebug
```

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

## Verification

Run the main checks with:

```bash
./gradlew lintDebug test assembleDebug
```

## Known Limitations

- Settings are kept in memory only, as persistence is not required for the
  assignment.
- Live weather depends on the Open-Meteo services and the device network.
- If a refresh fails after a successful live result for the same location, the
  app can show the last live conditions and asks the user to refresh again.
