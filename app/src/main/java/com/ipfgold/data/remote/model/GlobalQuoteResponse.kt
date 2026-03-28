package com.ipfgold.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Respuesta del endpoint `GLOBAL_QUOTE` de Alpha Vantage.
 *
 * Ejemplo:
 * ```json
 * {
 *   "Global Quote": {
 *     "01. symbol": "XAUUSD",
 *     "05. price": "4495.1500",
 *     "09. change": "89.8400",
 *     "10. change percent": "2.0394%",
 *     "07. latest trading day": "2026-03-27",
 *     "08. previous close": "4405.3100"
 *   }
 * }
 * ```
 */
@JsonClass(generateAdapter = true)
data class GlobalQuoteResponse(
    @Json(name = "Global Quote")
    val globalQuote: GlobalQuoteDto
)

@JsonClass(generateAdapter = true)
data class GlobalQuoteDto(
    @Json(name = "01. symbol")
    val symbol: String,
    @Json(name = "05. price")
    val price: String,
    @Json(name = "09. change")
    val change: String,
    @Json(name = "10. change percent")
    val changePercent: String,
    @Json(name = "07. latest trading day")
    val latestTradingDay: String,
    @Json(name = "08. previous close")
    val previousClose: String
)