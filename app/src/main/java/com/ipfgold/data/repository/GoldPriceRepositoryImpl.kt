package com.ipfgold.data.repository

import com.ipfgold.data.datasource.LocalGoldPriceDataSource
import com.ipfgold.data.datasource.RemoteGoldPriceDataSource
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.DataSourceException
import com.ipfgold.domain.repository.GoldPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementación del repositorio que sigue la estrategia «remoto → local».
 */
class GoldPriceRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteGoldPriceDataSource,
    private val localDataSource: LocalGoldPriceDataSource
) : GoldPriceRepository {

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
            cachedPrice ?: throw DataSourceException(
                "No se pudo obtener el precio del oro. " +
                        "Error de red: ${remoteException.message}. " +
                        "No hay datos en caché.",
                remoteException
            )
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
                throw DataSourceException(
                    "No se pudieron obtener los datos históricos. " +
                            "Error de red: ${remoteException.message}. " +
                            "No hay datos en caché.",
                    remoteException
                )
            }
        }
    }
}