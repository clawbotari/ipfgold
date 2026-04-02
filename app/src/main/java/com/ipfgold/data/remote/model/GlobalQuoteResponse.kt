package com.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

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
data class GlobalQuoteResponse(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuoteDto
)

data class GlobalQuoteDto(
    @SerializedName("01. symbol")
    val symbol: String,
    @SerializedName("05. price")
    val price: String,
    @SerializedName("09. change")
    val change: String,
    @SerializedName("10. change percent")
    val changePercent: String,
    @SerializedName("07. latest trading day")
    val latestTradingDay: String,
    @SerializedName("08. previous close")
    val previousClose: String
)