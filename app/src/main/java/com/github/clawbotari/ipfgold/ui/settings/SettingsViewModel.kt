package com.github.clawbotari.ipfgold.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.clawbotari.ipfgold.domain.model.Currency
import com.github.clawbotari.ipfgold.domain.model.DataSource
import com.github.clawbotari.ipfgold.domain.model.PricePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de configuración.
 *
 * Lee y escribe preferencias de usuario mediante DataStore.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        // Claves de preferencias
        private val KEY_CURRENCY = stringPreferencesKey("currency")
        private val KEY_PERIOD = stringPreferencesKey("period")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
        private val KEY_DATA_SOURCE = stringPreferencesKey("data_source")
        private val KEY_ALPHA_VANTAGE_API_KEY = stringPreferencesKey("alpha_vantage_api_key")
        private val KEY_METALS_API_KEY = stringPreferencesKey("metals_api_key")
        private val KEY_GOLD_API_KEY = stringPreferencesKey("gold_api_key")
    }

    // Valores por defecto
    private val defaultCurrency = Currency.USD.name
    private val defaultPeriod = PricePeriod.ALL.name
    private val defaultTheme = "system" // "system", "light", "dark"
    private val defaultRefreshInterval = 5 // minutos
    private val defaultDataSource = DataSource.ALPHA_VANTAGE.name
    private val defaultApiKey = ""

    // Flujos de preferencias
    val currency: Flow<Currency> = dataStore.data.map { prefs ->
        val value = prefs[KEY_CURRENCY] ?: defaultCurrency
        Currency.valueOf(value)
    }

    val period: Flow<PricePeriod> = dataStore.data.map { prefs ->
        val value = prefs[KEY_PERIOD] ?: defaultPeriod
        PricePeriod.valueOf(value)
    }

    val theme: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_THEME] ?: defaultTheme
    }

    val refreshInterval: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_INTERVAL] ?: defaultRefreshInterval
    }

    val dataSource: Flow<DataSource> = dataStore.data.map { prefs ->
        val value = prefs[KEY_DATA_SOURCE] ?: defaultDataSource
        DataSource.valueOf(value)
    }

    val alphaVantageApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_ALPHA_VANTAGE_API_KEY] ?: defaultApiKey
    }

    val metalsApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_METALS_API_KEY] ?: defaultApiKey
    }

    val goldApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_GOLD_API_KEY] ?: defaultApiKey
    }

    /**
     * Actualiza la moneda preferida.
     */
    fun setCurrency(currency: Currency) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_CURRENCY] = currency.name
            }
        }
    }

    /**
     * Actualiza el período predeterminado del gráfico.
     */
    fun setPeriod(period: PricePeriod) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_PERIOD] = period.name
            }
        }
    }

    /**
     * Actualiza el tema de la aplicación.
     *
     * @param theme Uno de: "system", "light", "dark".
     */
    fun setTheme(theme: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_THEME] = theme
            }
        }
    }

    /**
     * Actualiza el intervalo de actualización automática (en minutos).
     */
    fun setRefreshInterval(minutes: Int) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_REFRESH_INTERVAL] = minutes
            }
        }
    }

    /**
     * Actualiza la fuente de datos del precio del oro.
     */
    fun setDataSource(source: DataSource) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_DATA_SOURCE] = source.name
            }
        }
    }

    /**
     * Actualiza la API key de Alpha Vantage.
     */
    fun setAlphaVantageApiKey(apiKey: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_ALPHA_VANTAGE_API_KEY] = apiKey
            }
        }
    }

    /**
     * Actualiza la API key de Metals-API.
     */
    fun setMetalsApiKey(apiKey: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_METALS_API_KEY] = apiKey
            }
        }
    }

    /**
     * Actualiza la API key de GoldAPI.io.
     */
    fun setGoldApiKey(apiKey: String) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_GOLD_API_KEY] = apiKey
            }
        }
    }
}