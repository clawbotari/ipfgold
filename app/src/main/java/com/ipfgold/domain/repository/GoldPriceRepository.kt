package com.ipfgold.domain.repository

import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod

/**
 * Contrato para el repositorio de precios del oro.
 *
 * El repositorio sigue la estrategia «remoto → local»:
 * 1. Intenta obtener datos de la red.
 * 2. Si falla, devuelve la caché local (si existe).
 * 3. Si no hay caché, lanza una excepción descriptiva.
 */
interface GoldPriceRepository {

    /**
     * Obtiene el precio actual del oro.
     *
     * @return [GoldPrice] con precios en USD y EUR.
     * @throws DataSourceException Si fallan tanto la red como la caché.
     */
    suspend fun getCurrentPrice(): GoldPrice

    /**
     * Obtiene puntos históricos para el período especificado.
     *
     * @param period Período deseado (D1, W1, M1, Y1, ALL).
     * @return Lista de puntos ordenados por fecha ascendente.
     * @throws DataSourceException Si fallan tanto la red como la caché.
     */
    suspend fun getHistoricalPrices(period: PricePeriod): List<ChartPoint>
}