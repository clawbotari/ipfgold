package com.github.clawbotari.ipfgold.domain.repository

import com.github.clawbotari.ipfgold.domain.model.DataSource
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para acceder a las preferencias de configuración.
 */
interface SettingsRepository {

    /**
     * Flujo de la fuente de datos seleccionada.
     */
    val dataSource: Flow<DataSource>

    /**
     * Flujo de la API key de Alpha Vantage.
     */
    val alphaVantageApiKey: Flow<String>

    /**
     * Flujo de la API key de Metals-API.
     */
    val metalsApiKey: Flow<String>

    /**
     * Flujo de la API key de GoldAPI.io.
     */
    val goldApiKey: Flow<String>

    /**
     * Obtiene la fuente de datos actual (suspending).
     */
    suspend fun getDataSource(): DataSource

    /**
     * Obtiene la API key de Alpha Vantage actual (suspending).
     */
    suspend fun getAlphaVantageApiKey(): String

    /**
     * Obtiene la API key de Metals-API actual (suspending).
     */
    suspend fun getMetalsApiKey(): String

    /**
     * Obtiene la API key de GoldAPI.io actual (suspending).
     */
    suspend fun getGoldApiKey(): String

    /**
     * Actualiza la fuente de datos seleccionada.
     */
    suspend fun setDataSource(source: DataSource)

    /**
     * Actualiza la API key de Alpha Vantage.
     */
    suspend fun setAlphaVantageApiKey(apiKey: String)

    /**
     * Actualiza la API key de Metals-API.
     */
    suspend fun setMetalsApiKey(apiKey: String)

    /**
     * Actualiza la API key de GoldAPI.io.
     */
    suspend fun setGoldApiKey(apiKey: String)
}