package com.ipfgold.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ipfgold.data.local.dao.ChartPointDao
import com.ipfgold.data.local.dao.GoldPriceDao
import com.ipfgold.data.local.entity.ChartPointEntity
import com.ipfgold.data.local.entity.GoldPriceEntity

@Database(
    entities = [
        GoldPriceEntity::class,
        ChartPointEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goldPriceDao(): GoldPriceDao

    abstract fun chartPointDao(): ChartPointDao
}