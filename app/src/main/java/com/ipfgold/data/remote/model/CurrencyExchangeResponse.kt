package com.ipfgold.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Respuesta del endpoint `CURRENCY_EXCHANGE_RATE` de Alpha Vantage (USD→EUR).
 *
 * Ejemplo:
 * ```json
 * {
 *   "Realtime Currency Exchange Rate": {
 *     "5. Exchange Rate": "0.86884945",
 *     "6. Last Refreshed": "2026-03-28 20:16:03",
 *     "7. Time Zone": "UTC"
 *   }
 * }
 * ```
 */
@JsonClass(generateAdapter = true)
data class CurrencyExchangeResponse(
    @Json(name = "Realtime Currency Exchange Rate")
    val exchangeRate: ExchangeRateDto
)

@JsonClass(generateAdapter = true)
data class ExchangeRateDto(
    @Json(name = "5. Exchange Rate")
    val rate: String,
    @Json(name = "6. Last Refreshed")
    val lastRefreshed: String,
    @Json(name = "7. Time Zone")
    val timeZone: String
)