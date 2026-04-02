package com.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeResponse(
 @SerializedName("Realtime Currency Exchange Rate")
 val exchangeRate: ExchangeRateDto
)

data class ExchangeRateDto(
 @SerializedName("1. From_Currency Code")
 val fromCurrency: String,
 @SerializedName("2. From_Currency Name")
 val fromCurrencyName: String,
 @SerializedName("3. To_Currency Code")
 val toCurrency: String,
 @SerializedName("4. To_Currency Name")
 val toCurrencyName: String,
 @SerializedName("5. Exchange Rate")
 val rate: String,
 @SerializedName("6. Last Refreshed")
 val lastRefreshed: String,
 @SerializedName("7. Time Zone")
 val timeZone: String
)