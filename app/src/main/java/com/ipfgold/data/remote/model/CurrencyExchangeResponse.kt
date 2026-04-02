package com.ipfgold.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyExchangeResponse(
 @Json(name = "Realtime Currency Exchange Rate")
 val exchangeRate: ExchangeRateDto
)

@JsonClass(generateAdapter = true)
data class ExchangeRateDto(
 @Json(name = "1. From_Currency Code")
 val fromCurrency: String,
 @Json(name = "2. From_Currency Name")
 val fromCurrencyName: String,
 @Json(name = "3. To_Currency Code")
 val toCurrency: String,
 @Json(name = "4. To_Currency Name")
 val toCurrencyName: String,
 @Json(name = "5. Exchange Rate")
 val rate: String,
 @Json(name = "6. Last Refreshed")
 val lastRefreshed: String,
 @Json(name = "7. Time Zone")
 val timeZone: String
)