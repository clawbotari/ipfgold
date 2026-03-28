package com.ipfgold.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ipfgold.data.local.entity.ChartPointEntity
import java.time.LocalDate

@Dao
interface ChartPointDao {

    /**
     * Inserta o reemplaza una lista de puntos históricos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<ChartPointEntity>)

    /**
     * Obtiene todos los puntos dentro de un rango de fechas (inclusive).
     *
     * @param from Fecha inicial (inclusive).
     * @param to Fecha final (inclusive). Si es `null`, no se aplica límite superior.
     */
    @Query(
        "SELECT * FROM chart_points " +
        "WHERE date >= :from " +
        "AND (:to IS NULL OR date <= :to) " +
        "ORDER BY date ASC"
    )
    suspend fun getByDateRange(
        from: LocalDate,
        to: LocalDate? = null
    ): List<ChartPointEntity>

    /**
     * Elimina los puntos más antiguos que la fecha indicada.
     */
    @Query("DELETE FROM chart_points WHERE date < :olderThan")
    suspend fun deleteOlderThan(olderThan: LocalDate)

    /**
     * Elimina todos los puntos históricos (solo para pruebas/depuración).
     */
    @Query("DELETE FROM chart_points")
    suspend fun deleteAll()
}