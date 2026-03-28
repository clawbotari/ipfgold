package com.ipfgold.di

import android.content.Context
import androidx.room.Room
import com.ipfgold.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ipfgold.db"
        ).fallbackToDestructiveMigration()
         .build()

    @Provides
    @Singleton
    fun provideGoldPriceDao(database: AppDatabase) = database.goldPriceDao()

    @Provides
    @Singleton
    fun provideChartPointDao(database: AppDatabase) = database.chartPointDao()
}