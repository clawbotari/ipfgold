package com.github.clawbotari.ipfgold.di

import com.github.clawbotari.ipfgold.BuildConfig
import com.github.clawbotari.ipfgold.data.remote.api.AlphaVantageService
import com.github.clawbotari.ipfgold.data.remote.api.MetalsApiService
import com.github.clawbotari.ipfgold.data.remote.api.GoldApiService
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/"
    private const val METALS_API_BASE_URL = "https://metals-api.com/api/"
    private const val GOLD_API_BASE_URL = "https://www.goldapi.io/api/"

    @Provides
    @Singleton
    @Named("alphavantage")
    fun provideAlphaVantageOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val url = originalRequest.url.newBuilder()
                .addQueryParameter("apikey", BuildConfig.ALPHA_VANTAGE_API_KEY)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(url)
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("base")
    fun provideBaseOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("alphavantage")
    fun provideAlphaVantageRetrofit(@Named("alphavantage") okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(ALPHA_VANTAGE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("metalsapi")
    fun provideMetalsApiRetrofit(@Named("base") okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(METALS_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("goldapi")
    fun provideGoldApiRetrofit(@Named("base") okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(GOLD_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAlphaVantageService(@Named("alphavantage") retrofit: Retrofit): AlphaVantageService =
        retrofit.create(AlphaVantageService::class.java)

    @Provides
    @Singleton
    fun provideMetalsApiService(@Named("metalsapi") retrofit: Retrofit): MetalsApiService =
        retrofit.create(MetalsApiService::class.java)

    @Provides
    @Singleton
    fun provideGoldApiService(@Named("goldapi") retrofit: Retrofit): GoldApiService =
        retrofit.create(GoldApiService::class.java)
}