package com.github.clawbotari.ipfgold.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.clawbotari.ipfgold.domain.model.DataSource
import com.github.clawbotari.ipfgold.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación de [SettingsRepository] que utiliza DataStore.
 */
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        // Claves de preferencias (deben coincidir con SettingsViewModel)
        private val KEY_DATA_SOURCE = stringPreferencesKey("data_source")
        private val KEY_ALPHA_VANTAGE_API_KEY = stringPreferencesKey("alpha_vantage_api_key")
        private val KEY_METALS_API_KEY = stringPreferencesKey("metals_api_key")
        private val KEY_GOLD_API_KEY = stringPreferencesKey("gold_api_key")
        private val KEY_DEBUG_MODE = booleanPreferencesKey("debug_mode")

        // Valores por defecto
        private val DEFAULT_DATA_SOURCE = DataSource.ALPHA_VANTAGE.name
        private const val DEFAULT_API_KEY = ""
        private const val DEFAULT_DEBUG_MODE = false
    }

    override val dataSource: Flow<DataSource> = dataStore.data.map { prefs ->
        val value = prefs[KEY_DATA_SOURCE] ?: DEFAULT_DATA_SOURCE
        DataSource.valueOf(value)
    }

    override val alphaVantageApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_ALPHA_VANTAGE_API_KEY] ?: DEFAULT_API_KEY
    }

    override val metalsApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_METALS_API_KEY] ?: DEFAULT_API_KEY
    }

    override val goldApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_GOLD_API_KEY] ?: DEFAULT_API_KEY
    }

    override val debugMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DEBUG_MODE] ?: DEFAULT_DEBUG_MODE
    }

    override suspend fun getDataSource(): DataSource =
        dataSource.first()

    override suspend fun getAlphaVantageApiKey(): String =
        alphaVantageApiKey.first()

    override suspend fun getMetalsApiKey(): String =
        metalsApiKey.first()

    override suspend fun getGoldApiKey(): String =
        goldApiKey.first()

    override suspend fun setDataSource(source: DataSource) {
        dataStore.edit { prefs ->
            prefs[KEY_DATA_SOURCE] = source.name
        }
    }

    override suspend fun setAlphaVantageApiKey(apiKey: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ALPHA_VANTAGE_API_KEY] = apiKey
        }
    }

    override suspend fun setMetalsApiKey(apiKey: String) {
        dataStore.edit { prefs ->
            prefs[KEY_METALS_API_KEY] = apiKey
        }
    }

    override suspend fun setGoldApiKey(apiKey: String) {
        dataStore.edit { prefs ->
            prefs[KEY_GOLD_API_KEY] = apiKey
        }
    }

    override suspend fun getDebugMode(): Boolean =
        debugMode.first()

    override suspend fun setDebugMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_DEBUG_MODE] = enabled
        }
    }
}