package com.github.clawbotari.ipfgold.di

import com.github.clawbotari.ipfgold.data.datasource.LocalGoldPriceDataSource
import com.github.clawbotari.ipfgold.data.datasource.RemoteGoldPriceDataSource
import com.github.clawbotari.ipfgold.data.repository.GoldPriceRepositoryImpl
import com.github.clawbotari.ipfgold.data.repository.SettingsRepositoryImpl
import com.github.clawbotari.ipfgold.data.remote.mapper.MetalsApiMapper
import com.github.clawbotari.ipfgold.data.remote.mapper.GoldApiMapper
import com.github.clawbotari.ipfgold.domain.repository.GoldPriceRepository
import com.github.clawbotari.ipfgold.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGoldPriceRepository(
        remoteDataSource: RemoteGoldPriceDataSource,
        localDataSource: LocalGoldPriceDataSource
    ): GoldPriceRepository = GoldPriceRepositoryImpl(remoteDataSource, localDataSource)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository = impl

    @Provides
    @Singleton
    fun provideMetalsApiMapper(): MetalsApiMapper = MetalsApiMapper()

    @Provides
    @Singleton
    fun provideGoldApiMapper(): GoldApiMapper = GoldApiMapper()
}