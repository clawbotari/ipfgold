package com.github.clawbotari.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de Metals-API (/latest).
 *
 * Ejemplo de respuesta:
 * {
 *   "base": "XAU",
 *   "rates": {
 *     "USD": 1950.25,
 *     "EUR": 1780.50
 *   },
 *   "unit": "per ounce"
 * }
 */
data class MetalsApiResponse(
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: Map<String, Double>,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("timestamp")
    val timestamp: Long?
)