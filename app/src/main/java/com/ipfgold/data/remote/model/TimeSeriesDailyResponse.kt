package com.ipfgold.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
 * ```
 */
@JsonClass(generateAdapter = true)
data class TimeSeriesDailyResponse(
    @Json(name = "Meta Data")
    val metaData: MetaDataDto,
    @Json(name = "Time Series (Daily)")
    val timeSeries: Map<String, DailyDataDto>
)

@JsonClass(generateAdapter = true)
data class MetaDataDto(
    @Json(name = "1. Information")
    val information: String,
    @Json(name = "2. Symbol")
    val symbol: String,
    @Json(name = "3. Last Refreshed")
    val lastRefreshed: String,
    @Json(name = "4. Output Size")
    val outputSize: String,
    @Json(name = "5. Time Zone")
    val timeZone: String
)

@JsonClass(generateAdapter = true)
data class DailyDataDto(
    @Json(name = "1. open")
    val open: String,
    @Json(name = "2. high")
    val high: String,
    @Json(name = "3. low")
    val low: String,
    @Json(name = "4. close")
    val close: String,
    @Json(name = "5. volume")
    val volume: String
)