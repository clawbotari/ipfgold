package com.github.clawbotari.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de GoldAPI.io (/api/price/XAU).
 *
 * Ejemplo de respuesta:
 * {
 *   "price": 3300.0,
 *   "ch": -12.5,
 *   "chp": -0.38,
 *   "symbol": "XAUUSD",
 *   "timestamp": 1617225600
 * }
 */
data class GoldApiResponse(
    @SerializedName("price")
    val price: Double,
    @SerializedName("ch")
    val ch: Double?,
    @SerializedName("chp")
    val chp: Double?,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("timestamp")
    val timestamp: Long
)