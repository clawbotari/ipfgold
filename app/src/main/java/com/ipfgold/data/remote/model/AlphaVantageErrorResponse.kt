package com.ipfgold.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * DTO genérico para respuestas de error de Alpha Vantage.
 *
 * Alpha Vantage devuelve errores de límite de API y throttling con campos "Information" o "Note".
 *
 * Ejemplos:
 * ```json
 * {"Information": "We have detected your API key...standard API rate limit is 25 requests per day..."}
 * ```
 * ```json
 * {"Note": "Thank you for using Alpha Vantage! Our standard API call frequency is..."}
 * ```
 */
data class AlphaVantageErrorResponse(
    @SerializedName("Information")
    val information: String?,
    @SerializedName("Note")
    val note: String?
)