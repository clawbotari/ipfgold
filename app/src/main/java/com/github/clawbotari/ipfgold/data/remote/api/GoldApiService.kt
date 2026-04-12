package com.github.clawbotari.ipfgold.data.remote.api

import com.github.clawbotari.ipfgold.data.remote.model.GoldApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Servicio Retrofit para GoldAPI.io (https://www.goldapi.io/).
 *
 * Documentación: https://www.goldapi.io/documentation
 */
interface GoldApiService {

    /**
     * Obtiene el precio actual del oro (XAU) en USD.
     * La conversión a EUR se hace localmente usando tasas de cambio.
     *
     * @param apiKey API key de GoldAPI.io (header `x-access-token`).
     */
    @GET("price/XAU")
    suspend fun getGoldPrice(
        @Header("x-access-token") apiKey: String
    ): GoldApiResponse
}