package com.github.clawbotari.ipfgold.data.remote.api

import com.github.clawbotari.ipfgold.data.remote.model.MetalsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio Retrofit para Metals-API (https://metals-api.com/).
 *
 * Documentación: https://metals-api.com/documentation
 */
interface MetalsApiService {

    /**
     * Obtiene el precio más reciente del oro (XAU) en USD y EUR.
     *
     * @param apiKey API key de Metals-API.
     * @param base Metal base (XAU = oro).
     * @param currencies Monedas destino (USD, EUR).
     */
    @GET("latest")
    suspend fun getLatestPrice(
        @Query("api_key") apiKey: String,
        @Query("base") base: String = "XAU",
        @Query("currencies") currencies: String = "USD,EUR"
    ): MetalsApiResponse
}