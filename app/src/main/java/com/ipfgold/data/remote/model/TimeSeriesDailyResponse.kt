package com.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint `TIME_SERIES_DAILY` de Alpha Vantage.
 *
 * Ejemplo:
 * ```json
 * {
 *   "Meta Data": {
 *     "1. Information": "Daily Prices (open, high, low, close) and Volumes",
 *     "2. Symbol": "XAUUSD",
 *     "3. Last Refreshed": "2026-03-27",
 *     "4. Output Size": "Compact",
 *     "5. Time Zone": "US/Eastern"
 *   },
 *   "Time Series (Daily)": {
 *     "2026-03-27": {
 *       "1. open": "4405.3400",
 *       "2. high": "4555.1400",
 *       "3. low": "4375.4700",
 *       "4. close": "4495.1500",
 *       "5. volume": "0"
 *     }
 *   }
 * }
 */
data class TimeSeriesDailyResponse(
    @SerializedName("Meta Data")
    val metaData: MetaDataDto?,
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, DailyDataDto>?
)

data class MetaDataDto(
    @SerializedName("1. Information")
    val information: String?,
    @SerializedName("2. Symbol")
    val symbol: String?,
    @SerializedName("3. Last Refreshed")
    val lastRefreshed: String?,
    @SerializedName("4. Output Size")
    val outputSize: String?,
    @SerializedName("5. Time Zone")
    val timeZone: String?
)

data class DailyDataDto(
    @SerializedName("1. open")
    val open: String?,
    @SerializedName("2. high")
    val high: String?,
    @SerializedName("3. low")
    val low: String?,
    @SerializedName("4. close")
    val close: String?,
    @SerializedName("5. volume")
    val volume: String?
)