package com.github.clawbotari.ipfgold.data.datasource

import com.github.clawbotari.ipfgold.data.remote.api.AlphaVantageService
import com.github.clawbotari.ipfgold.data.remote.api.MetalsApiService
import com.github.clawbotari.ipfgold.data.remote.api.GoldApiService
import com.github.clawbotari.ipfgold.data.remote.mapper.ChartPointMapper
import com.github.clawbotari.ipfgold.data.remote.mapper.GoldPriceMapper
import com.github.clawbotari.ipfgold.data.remote.mapper.MetalsApiMapper
import com.github.clawbotari.ipfgold.data.remote.mapper.GoldApiMapper
import com.github.clawbotari.ipfgold.domain.model.ChartPoint
import com.github.clawbotari.ipfgold.domain.model.DataSource
import com.github.clawbotari.ipfgold.domain.model.GoldPrice
import com.github.clawbotari.ipfgold.domain.model.PricePeriod
import com.github.clawbotari.ipfgold.domain.repository.DataSourceException
import com.github.clawbotari.ipfgold.domain.repository.SettingsRepository
import com.github.clawbotari.ipfgold.utils.DebugLogger
import com.github.clawbotari.ipfgold.utils.LogType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

/**
 * Fuente de datos remota que selecciona entre Alpha Vantage, Metals-API y GoldAPI.io.
 *
 * La fuente activa se determina por la preferencia guardada en [SettingsRepository].
 * Para datos históricos, solo Alpha Vantage proporciona serie temporal;
 * Metals-API y GoldAPI.io devuelven datos de demostración.
 */
class RemoteGoldPriceDataSource @Inject constructor(
    private val alphaVantageService: AlphaVantageService,
    private val metalsApiService: MetalsApiService,
    private val goldApiService: GoldApiService,
    private val goldPriceMapper: GoldPriceMapper,
    private val metalsApiMapper: MetalsApiMapper,
    private val goldApiMapper: GoldApiMapper,
    private val chartPointMapper: ChartPointMapper,
    private val settingsRepository: SettingsRepository,
    private val debugLogger: DebugLogger
) {

    companion object {
        private const val FALLBACK_EXCHANGE_RATE = 0.92
    }

    /**
     * Obtiene el precio actual del oro desde la fuente seleccionada.
     */
    suspend fun getCurrentPrice(): GoldPrice {
        val dataSource = settingsRepository.getDataSource()
        debugLogger.log(LogType.REQUEST, "[$dataSource] Fetching gold price...")
        return when (dataSource) {
            DataSource.ALPHA_VANTAGE -> fetchFromAlphaVantage()
            DataSource.METALS_API -> fetchFromMetalsApi()
            DataSource.GOLD_API -> fetchFromGoldApi()
        }
    }

    /**
     * Obtiene puntos históricos desde la fuente seleccionada.
     *
     * Solo Alpha Vantage proporciona datos históricos reales.
     * Metals-API y GoldAPI.io devuelven datos de demostración.
     */
    suspend fun getHistoricalPrices(period: PricePeriod): List<ChartPoint> {
        val dataSource = settingsRepository.getDataSource()
        debugLogger.log(LogType.REQUEST, "[$dataSource] Fetching historical prices...")
        return when (dataSource) {
            DataSource.ALPHA_VANTAGE -> fetchHistoricalFromAlphaVantage(period)
            DataSource.METALS_API -> createDemoChartPoints(period)
            DataSource.GOLD_API -> createDemoChartPoints(period)
        }
    }

    /**
     * Obtiene el tipo de cambio USD → EUR desde Alpha Vantage.
     * Solo se usa cuando la fuente es Alpha Vantage.
     */
    private suspend fun getExchangeRate(): Double = try {
        val exchange = alphaVantageService.getCurrencyExchangeRate()
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

    private suspend fun fetchFromAlphaVantage(): GoldPrice = try {
        coroutineScope {
            val quoteDeferred = async { alphaVantageService.getGlobalQuote() }
            val exchangeRateDeferred = async { getExchangeRate() }

            val quote = quoteDeferred.await()
            val exchangeRate = exchangeRateDeferred.await()

            val price = goldPriceMapper.toGoldPrice(quote, exchangeRate)
            debugLogger.log(LogType.RESPONSE, "[Alpha Vantage] Price: ${price.priceUSD} USD, ${price.priceEUR} EUR")
            price
        }
    } catch (e: HttpException) {
        debugLogger.log(LogType.ERROR, "[Alpha Vantage] HTTP error: ${e.code()} - ${e.message()}")
        throw DataSourceException("HTTP error fetching gold price from Alpha Vantage: ${e.code()}", e)
    } catch (e: Exception) {
        debugLogger.log(LogType.ERROR, "[Alpha Vantage] Network error: ${e.javaClass.simpleName}: ${e.message}")
        throw DataSourceException(
            "Network error fetching gold price from Alpha Vantage: ${e.javaClass.simpleName}: ${e.message} | cause: ${e.cause?.message}",
            e
        )
    }

    private suspend fun fetchFromMetalsApi(): GoldPrice = try {
        val apiKey = settingsRepository.getMetalsApiKey()
        val response = metalsApiService.getLatestPrice(apiKey)
        val price = metalsApiMapper.toGoldPrice(response)
        debugLogger.log(LogType.RESPONSE, "[Metals-API] Price: ${price.priceUSD} USD, ${price.priceEUR} EUR")
        price
    } catch (e: HttpException) {
        debugLogger.log(LogType.ERROR, "[Metals-API] HTTP error: ${e.code()} - ${e.message()}")
        throw DataSourceException("HTTP error fetching gold price from Metals-API: ${e.code()}", e)
    } catch (e: Exception) {
        debugLogger.log(LogType.ERROR, "[Metals-API] Network error: ${e.javaClass.simpleName}: ${e.message}")
        throw DataSourceException(
            "Network error fetching gold price from Metals-API: ${e.javaClass.simpleName}: ${e.message}",
            e
        )
    }

    private suspend fun fetchFromGoldApi(): GoldPrice = try {
        val apiKey = settingsRepository.getGoldApiKey()
        val response = goldApiService.getGoldPrice(apiKey)
        val price = goldApiMapper.toGoldPrice(response)
        debugLogger.log(LogType.RESPONSE, "[GoldAPI.io] Price: ${price.priceUSD} USD, ${price.priceEUR} EUR, change: ${price.change24h} (${price.changePercent24h}%)")
        price
    } catch (e: HttpException) {
        debugLogger.log(LogType.ERROR, "[GoldAPI.io] HTTP error: ${e.code()} - ${e.message()}")
        throw DataSourceException("HTTP error fetching gold price from GoldAPI.io: ${e.code()}", e)
    } catch (e: Exception) {
        debugLogger.log(LogType.ERROR, "[GoldAPI.io] Network error: ${e.javaClass.simpleName}: ${e.message}")
        throw DataSourceException(
            "Network error fetching gold price from GoldAPI.io: ${e.javaClass.simpleName}: ${e.message}",
            e
        )
    }

    private suspend fun fetchHistoricalFromAlphaVantage(period: PricePeriod): List<ChartPoint> = try {
        coroutineScope {
            val seriesDeferred = async { alphaVantageService.getTimeSeriesDaily() }
            val exchangeRateDeferred = async { getExchangeRate() }

            val series = seriesDeferred.await()
            val exchangeRate = exchangeRateDeferred.await()

            val points = chartPointMapper.toChartPoints(series, exchangeRate, period)
            debugLogger.log(LogType.RESPONSE, "[Alpha Vantage] Historical points: ${points.size} points")
            points
        }
    } catch (e: HttpException) {
        debugLogger.log(LogType.ERROR, "[Alpha Vantage] HTTP error fetching historical: ${e.code()} - ${e.message()}")
        throw DataSourceException("HTTP error fetching historical prices from Alpha Vantage: ${e.code()}", e)
    } catch (e: Exception) {
        debugLogger.log(LogType.ERROR, "[Alpha Vantage] Network error fetching historical: ${e.javaClass.simpleName}: ${e.message}")
        throw DataSourceException(
            "Network error fetching historical prices from Alpha Vantage: ${e.javaClass.simpleName}: ${e.message}",
            e
        )
    }

    /**
     * Genera puntos históricos de demostración cuando la fuente no los proporciona.
     */
    private fun createDemoChartPoints(period: PricePeriod): List<ChartPoint> {
        debugLogger.log(LogType.INFO, "[Demo] Generating demo chart points for period: $period")
        // Lógica simplificada de demostración (la misma que en GoldPriceRepositoryImpl)
        val points = mutableListOf<ChartPoint>()
        val today = java.time.LocalDate.now()
        val demoPriceUSD = 3300.0
        val demoExchangeRate = 0.92
        // 30 puntos, desde hace 29 días hasta hoy
        for (i in 29 downTo 0) {
            val date = today.minusDays(i.toLong())
            val base = demoPriceUSD - 100.0 + (i * 6.9)
            val priceUSD = base + (Math.sin(i * 0.5) * 50.0)
            val priceEUR = priceUSD * demoExchangeRate
            points.add(
                ChartPoint(
                    date = date,
                    priceUSD = priceUSD,
                    priceEUR = priceEUR,
                    isDemo = true
                )
            )
        }
        debugLogger.log(LogType.INFO, "[Demo] Generated ${points.size} demo points")
        return points
    }
}