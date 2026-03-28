package com.ipfgold.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ipfgold.data.local.entity.GoldPriceEntity
import java.time.Instant

@Dao
interface GoldPriceDao {

    /**
     * Inserta o reemplaza un precio en caché.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: GoldPriceEntity)

    /**
     * Obtiene el precio más reciente que aún no ha expirado.
     *
     * @return La entidad más reciente, o `null` si no hay datos válidos.
     */
    @Query(
        "SELECT * FROM gold_prices " +
        "WHERE expires_at > :now " +
        "ORDER BY timestamp DESC " +
        "LIMIT 1"
    )
    suspend fun getLatestValid(now: Instant = Instant.now()): GoldPriceEntity?

    /**
     * Elimina todas las entradas cuya fecha de expiración haya pasado.
     */
    @Query("DELETE FROM gold_prices WHERE expires_at <= :now")
    suspend fun deleteExpired(now: Instant = Instant.now())

    /**
     * Elimina todos los precios almacenados (solo para pruebas/depuración).
     */
    @Query("DELETE FROM gold_prices")
    suspend fun deleteAll()
}