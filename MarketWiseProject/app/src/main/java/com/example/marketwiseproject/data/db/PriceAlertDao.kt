package com.example.marketwiseproject.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE symbol = :symbol")
    fun getAlertsBySymbol(symbol: String): Flow<List<PriceAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity)

    @Delete
    suspend fun deleteAlert(alert: PriceAlertEntity)

    @Query("UPDATE price_alerts SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleAlert(id: Int, isEnabled: Boolean)
}
