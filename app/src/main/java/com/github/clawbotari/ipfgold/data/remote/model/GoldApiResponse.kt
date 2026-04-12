package com.github.clawbotari.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API de GoldAPI.io (/api/price/XAU).
 *
 * Ejemplo de respuesta:
 * {
 *   "price": 1950.25,
 *   "price_gram_24k": 62.70,
 *   "price_gram_22k": 57.50,
 *   "price_gram_21k": 54.90,
 *   "price_gram_20k": 52.30,
 *   "price_gram_18k": 47.10,
 *   "price_gram_16k": 41.90,
 *   "price_gram_14k": 36.70,
 *   "price_gram_10k": 26.20,
 *   "timestamp": 1617225600
 * }
 */
data class GoldApiResponse(
    @SerializedName("price")
    val price: Double,
    @SerializedName("price_gram_24k")
    val priceGram24k: Double?,
    @SerializedName("price_gram_22k")
    val priceGram22k: Double?,
    @SerializedName("price_gram_21k")
    val priceGram21k: Double?,
    @SerializedName("price_gram_20k")
    val priceGram20k: Double?,
    @SerializedName("price_gram_18k")
    val priceGram18k: Double?,
    @SerializedName("price_gram_16k")
    val priceGram16k: Double?,
    @SerializedName("price_gram_14k")
    val priceGram14k: Double?,
    @SerializedName("price_gram_10k")
    val priceGram10k: Double?,
    @SerializedName("timestamp")
    val timestamp: Long
)