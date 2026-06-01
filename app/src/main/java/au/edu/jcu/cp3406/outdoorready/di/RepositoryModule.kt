package au.edu.jcu.cp3406.outdoorready.di

import au.edu.jcu.cp3406.outdoorready.data.repository.DefaultWeatherRepository
import au.edu.jcu.cp3406.outdoorready.data.repository.InMemorySettingsRepository
import au.edu.jcu.cp3406.outdoorready.data.repository.SettingsRepository
import au.edu.jcu.cp3406.outdoorready.data.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        repository: InMemorySettingsRepository,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        repository: DefaultWeatherRepository,
    ): WeatherRepository
}

