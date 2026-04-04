package com.ipfgold.data.datasource

import com.ipfgold.data.local.dao.ChartPointDao
import com.ipfgold.data.local.dao.GoldPriceDao
import com.ipfgold.data.local.entity.ChartPointEntity
import com.ipfgold.data.local.entity.GoldPriceEntity
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.DataSourceException
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Fuente de datos local que usa Room como caché.
 *
 * Aplica TTL (time‑to‑live) para evitar datos obsoletos:
 * - Precios actuales: 5 minutos.
 * - Puntos históricos: 24 horas.
 */
class LocalGoldPriceDataSource @Inject constructor(
    private val goldPriceDao: GoldPriceDao,
    private val chartPointDao: ChartPointDao
) {

    companion object {
        private const val CURRENT_PRICE_TTL_MINUTES = 60L  // 1 hora
        private const val HISTORICAL_DATA_TTL_DAYS = 7L    // 1 semana
    }

    // ---------- Precio actual ----------

    /**
     * Guarda un precio en caché, reemplazando cualquier entrada anterior.
     */
    suspend fun saveCurrentPrice(price: GoldPrice) {
        val entity = GoldPriceEntity.create(
            priceUSD = price.priceUSD,
            priceEUR = price.priceEUR,
            change24h = price.change24h,
            changePercent24h = price.changePercent24h,
            timestamp = price.timestamp,
            ttlMinutes = CURRENT_PRICE_TTL_MINUTES
        )
        goldPriceDao.insert(entity)
        goldPriceDao.deleteExpired()
    }

    /**
     * Recupera el precio más reciente de la caché, si existe y no ha expirado.
     */
    suspend fun getCurrentPrice(): GoldPrice? {
        val entity = goldPriceDao.getLatestValid() ?: return null
        return GoldPrice(
            priceUSD = entity.priceUSD,
            priceEUR = entity.priceEUR,
            change24h = entity.change24h,
            changePercent24h = entity.changePercent24h,
            timestamp = entity.timestamp
        )
    }

    // ---------- Datos históricos ----------

    /**
     * Guarda una lista de puntos históricos, reemplazando los existentes.
     * Elimina los puntos más antiguos que el TTL.
     */
    suspend fun saveHistoricalPoints(points: List<ChartPoint>) {
        val entities = points.map { point ->
            ChartPointEntity(
                date = point.date,
                priceUSD = point.priceUSD,
                priceEUR = point.priceEUR
            )
        }
        chartPointDao.insertAll(entities)

        val cutoff = LocalDate.now().minusDays(HISTORICAL_DATA_TTL_DAYS)
        chartPointDao.deleteOlderThan(cutoff)
    }

    /**
     * Recupera puntos históricos para el período solicitado.
     * Solo devuelve puntos que estén dentro del TTL.
     */
    suspend fun getHistoricalPoints(period: PricePeriod): List<ChartPoint> {
        val startDate = calculateStartDate(period)
        val entities = chartPointDao.getByDateRange(startDate)

        return entities.map { entity ->
            ChartPoint(
                date = entity.date,
                priceUSD = entity.priceUSD,
                priceEUR = entity.priceEUR
            )
        }
    }

    private fun calculateStartDate(period: PricePeriod): LocalDate {
        val today = LocalDate.now()
        return when (period) {
            PricePeriod.D1 -> today.minusDays(1)
            PricePeriod.W1 -> today.minusWeeks(1)
            PricePeriod.M1 -> today.minusMonths(1)
            PricePeriod.Y1 -> today.minusYears(1)
            PricePeriod.ALL -> LocalDate.MIN
        }
    }
}