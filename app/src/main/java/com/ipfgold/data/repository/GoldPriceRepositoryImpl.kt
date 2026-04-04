package com.ipfgold.data.repository

import com.ipfgold.data.datasource.LocalGoldPriceDataSource
import com.ipfgold.data.datasource.RemoteGoldPriceDataSource
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.DataSourceException
import com.ipfgold.domain.repository.GoldPriceRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementación del repositorio que sigue la estrategia «remoto → local».
 *
 * Si tanto la red como la caché fallan, devuelve datos de demostración (demo)
 * para que la aplicación siga funcionando durante el desarrollo o cuando
 * se agota el límite de la API.
 */
class GoldPriceRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteGoldPriceDataSource,
    private val localDataSource: LocalGoldPriceDataSource
) : GoldPriceRepository {

    companion object {
        /**
         * Precio de demostración (USD).
         */
        private const val DEMO_PRICE_USD = 3300.0

        /**
         * Tasa de cambio de demostración (USD → EUR).
         */
        private const val DEMO_EXCHANGE_RATE = 0.92

        /**
         * Precio de demostración en EUR.
         */
        private val DEMO_PRICE_EUR = DEMO_PRICE_USD * DEMO_EXCHANGE_RATE

        /**
         * Variación de 24 h de demostración (USD).
         */
        private const val DEMO_CHANGE_24H = -12.5

        /**
         * Variación porcentual de 24 h de demostración.
         */
        private const val DEMO_CHANGE_PERCENT_24H = -0.38

        /**
         * Genera un precio de oro de demostración.
         */
        private fun createDemoGoldPrice(): GoldPrice = GoldPrice(
            priceUSD = DEMO_PRICE_USD,
            priceEUR = DEMO_PRICE_EUR,
            change24h = DEMO_CHANGE_24H,
            changePercent24h = DEMO_CHANGE_PERCENT_24H,
            timestamp = Instant.now(),
            isDemo = true
        )

        /**
         * Genera puntos históricos de demostración.
         *
         * @param period Período solicitado (se ignora, siempre se generan 30 puntos).
         */
        private fun createDemoChartPoints(period: PricePeriod): List<ChartPoint> {
            val points = mutableListOf<ChartPoint>()
            val today = LocalDate.now()
            // 30 puntos, desde hace 29 días hasta hoy
            for (i in 29 downTo 0) {
                val date = today.minusDays(i.toLong())
                // Precio USD oscila entre 3200 y 3400
                val base = DEMO_PRICE_USD - 100.0 + (i * 6.9)  // ligera tendencia ascendente
                val priceUSD = base + (Math.sin(i * 0.5) * 50.0)  // fluctuación sinusoidal
                val priceEUR = priceUSD * DEMO_EXCHANGE_RATE
                points.add(
                    ChartPoint(
                        date = date,
                        priceUSD = priceUSD,
                        priceEUR = priceEUR,
                        isDemo = true
                    )
                )
            }
            return points
        }
    }

    override suspend fun getCurrentPrice(): GoldPrice {
        return try {
            // 1. Intenta obtener de la red
            val remotePrice = remoteDataSource.getCurrentPrice()
            // 2. Guarda en caché
            localDataSource.saveCurrentPrice(remotePrice)
            // 3. Devuelve el precio remoto
            remotePrice
        } catch (remoteException: DataSourceException) {
            // 4. Si falla la red, intenta la caché local
            val cachedPrice = localDataSource.getCurrentPrice()
            cachedPrice ?: createDemoGoldPrice()
        }
    }

    override suspend fun getHistoricalPrices(period: PricePeriod): List<ChartPoint> {
        return try {
            // 1. Intenta obtener de la red
            val remotePoints = remoteDataSource.getHistoricalPrices(period)
            // 2. Guarda en caché
            localDataSource.saveHistoricalPoints(remotePoints)
            // 3. Devuelve los puntos remotos
            remotePoints
        } catch (remoteException: DataSourceException) {
            // 4. Si falla la red, intenta la caché local
            val cachedPoints = localDataSource.getHistoricalPoints(period)
            if (cachedPoints.isNotEmpty()) {
                cachedPoints
            } else {
                createDemoChartPoints(period)
            }
        }
    }
}