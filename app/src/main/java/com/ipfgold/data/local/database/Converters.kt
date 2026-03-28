package com.ipfgold.data.local.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Convertidores de tipos para Room.
 */
object Converters {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromInstant(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.format(dateFormatter)

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it, dateFormatter) }
}