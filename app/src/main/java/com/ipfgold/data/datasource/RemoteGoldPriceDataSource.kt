package com.ipfgold.data.datasource

import com.ipfgold.data.remote.api.AlphaVantageService
import com.ipfgold.data.remote.mapper.ChartPointMapper
import com.ipfgold.data.remote.mapper.GoldPriceMapper
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.DataSourceException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Fuente de datos remota que consume la API de Alpha Vantage.
 *
 * Maneja errores HTTP y de red, lanzando [DataSourceException] cuando falla.
 */
class RemoteGoldPriceDataSource @Inject constructor(
    private val service: AlphaVantageService,
    private val goldPriceMapper: GoldPriceMapper,
    private val chartPointMapper: ChartPointMapper
) {

    /**
     * Obtiene el precio actual del oro desde la red.
     */
    suspend fun getCurrentPrice(): GoldPrice = try {
        coroutineScope {
            val quoteDeferred = async { service.getGlobalQuote() }
            val exchangeDeferred = async { service.getCurrencyExchangeRate() }

            val quote = quoteDeferred.await()
            val exchange = exchangeDeferred.await()

            goldPriceMapper.toGoldPrice(quote, exchange)
        }
    } catch (e: HttpException) {
        throw DataSourceException("HTTP error fetching gold price: ${e.code()}", e)
    } catch (e: Exception) {
        throw DataSourceException("Network error fetching gold price", e)
    }

    /**
     * Obtiene puntos históricos desde la red para el período especificado.
     */
    suspend fun getHistoricalPrices(period: PricePeriod): List<ChartPoint> = try {
        coroutineScope {
            val seriesDeferred = async { service.getTimeSeriesDaily() }
            val exchangeDeferred = async { service.getCurrencyExchangeRate() }

            val series = seriesDeferred.await()
            val exchange = exchangeDeferred.await()
            val exchangeRate = exchange.exchangeRate.rate.toDouble()

            chartPointMapper.toChartPoints(series, exchangeRate, period)
        }
    } catch (e: HttpException) {
        throw DataSourceException("HTTP error fetching historical prices: ${e.code()}", e)
    } catch (e: Exception) {
        throw DataSourceException("Network error fetching historical prices", e)
    }
}