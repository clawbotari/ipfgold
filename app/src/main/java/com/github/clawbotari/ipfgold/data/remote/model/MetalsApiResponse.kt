package com.github.clawbotari.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de Metals-API (/latest).
 *
 * Ejemplo de respuesta:
 * {
 *   "success": true,
 *   "base": "XAU",
 *   "rates": {
 *     "USD": 3300.0,
 *     "EUR": 3025.0
 *   },
 *   "unit": "per ounce"
 * }
 */
data class MetalsApiResponse(
    @SerializedName("success")
    val success: Boolean?,
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: Map<String, Double>?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("timestamp")
    val timestamp: Long?
)