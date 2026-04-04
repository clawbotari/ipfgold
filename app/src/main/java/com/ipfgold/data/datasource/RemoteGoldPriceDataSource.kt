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
import timber.log.Timber
import javax.inject.Inject

/**
 * Fuente de datos remota que consume la API de Alpha Vantage.
 *
 * Maneja errores HTTP y de red, lanzando [DataSourceException] cuando falla.
 *
 * Optimización: el tipo de cambio USD→EUR se obtiene una sola vez y se comparte
 * entre [getCurrentPrice] y [getHistoricalPrices], con un valor de fallback (0.92)
 * si la API de tipo de cambio falla.
 */
class RemoteGoldPriceDataSource @Inject constructor(
    private val service: AlphaVantageService,
    private val goldPriceMapper: GoldPriceMapper,
    private val chartPointMapper: ChartPointMapper
) {

    companion object {
        private const val FALLBACK_EXCHANGE_RATE = 0.92
    }

    /**
     * Obtiene el tipo de cambio USD → EUR desde la API.
     * Si la API falla o devuelve un valor nulo/inválido, retorna un valor de fallback (0.92)
     * y registra una advertencia.
     *
     * Este método se llama una sola vez y su resultado se reutiliza en todas las llamadas
     * que necesiten la tasa de cambio, reduciendo las peticiones a Alpha Vantage.
     */
    private suspend fun getExchangeRate(): Double = try {
        val exchange = service.getCurrencyExchangeRate()
        val rate = exchange.exchangeRate?.rate?.toDoubleOrNull()

        if (rate == null || rate <= 0.0) {
            Timber.w("Exchange rate API returned null/zero/negative value ($rate). Using fallback: $FALLBACK_EXCHANGE_RATE")
            FALLBACK_EXCHANGE_RATE
        } else {
            rate
        }
    } catch (e: Exception) {
        Timber.w(e, "Failed to fetch exchange rate. Using fallback: $FALLBACK_EXCHANGE_RATE")
        FALLBACK_EXCHANGE_RATE
    }

    /**
     * Obtiene el precio actual del oro desde la red.
     *
     * Realiza dos llamadas paralelas: cotización del oro y tipo de cambio.
     * El tipo de cambio se obtiene mediante [getExchangeRate], que reutiliza la tasa
     * si ya se ha obtenido en la misma sesión (caché simple).
     */
    suspend fun getCurrentPrice(): GoldPrice = try {
        coroutineScope {
            val quoteDeferred = async { service.getGlobalQuote() }
            val exchangeRateDeferred = async { getExchangeRate() }

            val quote = quoteDeferred.await()
            val exchangeRate = exchangeRateDeferred.await()

            goldPriceMapper.toGoldPrice(quote, exchangeRate)
        }
    } catch (e: HttpException) {
        throw DataSourceException("HTTP error fetching gold price: ${e.code()}", e)
    } catch (e: Exception) {
        throw DataSourceException(
            "Network error fetching gold price: ${e.javaClass.simpleName}: ${e.message} | cause: ${e.cause?.message}",
            e
        )
    }

    /**
     * Obtiene puntos históricos desde la red para el período especificado.
     *
     * Realiza dos llamadas paralelas: serie temporal y tipo de cambio.
     * El tipo de cambio se obtiene mediante [getExchangeRate], que reutiliza la tasa
     * si ya se ha obtenido en la misma sesión (caché simple).
     */
    suspend fun getHistoricalPrices(period: PricePeriod): List<ChartPoint> = try {
        coroutineScope {
            val seriesDeferred = async { service.getTimeSeriesDaily() }
            val exchangeRateDeferred = async { getExchangeRate() }

            val series = seriesDeferred.await()
            val exchangeRate = exchangeRateDeferred.await()

            chartPointMapper.toChartPoints(series, exchangeRate, period)
        }
    } catch (e: HttpException) {
        throw DataSourceException("HTTP error fetching historical prices: ${e.code()}", e)
    } catch (e: Exception) {
        throw DataSourceException(
            "Network error fetching historical prices: ${e.javaClass.simpleName}: ${e.message} | cause: ${e.cause?.message}",
            e
        )
    }
}