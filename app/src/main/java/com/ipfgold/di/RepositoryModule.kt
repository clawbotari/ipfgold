package com.ipfgold.di

import com.ipfgold.data.datasource.LocalGoldPriceDataSource
import com.ipfgold.data.datasource.RemoteGoldPriceDataSource
import com.ipfgold.data.repository.GoldPriceRepositoryImpl
import com.ipfgold.domain.repository.GoldPriceRepository
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
}