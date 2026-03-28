package com.ipfgold.data.remote.api

import com.ipfgold.data.remote.model.CurrencyExchangeResponse
import com.ipfgold.data.remote.model.GlobalQuoteResponse
import com.ipfgold.data.remote.model.TimeSeriesDailyResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio Retrofit para la API de Alpha Vantage.
 *
 * La API key se añade automáticamente mediante un interceptor OkHttp.
 * Todos los endpoints usan el símbolo `XAUUSD` (oro en USD).
 */
interface AlphaVantageService {

    /**
     * Obtiene la cotización actual del oro (XAUUSD).
     *
     * @param symbol Símbolo del activo (por defecto `XAUUSD`).
     */
    @GET("query")
    suspend fun getGlobalQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String = "XAUUSD"
    ): GlobalQuoteResponse

    /**
     * Obtiene el histórico diario de precios del oro (XAUUSD).
     *
     * @param symbol Símbolo del activo (por defecto `XAUUSD`).
     * @param outputSize Tamaño de salida (`compact` = últimos 100 puntos, `full` = 20+ años).
     */
    @GET("query")
    suspend fun getTimeSeriesDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String = "XAUUSD",
        @Query("outputsize") outputSize: String = "compact"
    ): TimeSeriesDailyResponse

    /**
     * Obtiene la tasa de cambio USD → EUR.
     *
     * @param fromCurrency Código de moneda origen (por defecto `USD`).
     * @param toCurrency Código de moneda destino (por defecto `EUR`).
     */
    @GET("query")
    suspend fun getCurrencyExchangeRate(
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("from_currency") fromCurrency: String = "USD",
        @Query("to_currency") toCurrency: String = "EUR"
    ): CurrencyExchangeResponse
}