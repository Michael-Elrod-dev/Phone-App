package com.mselrod.billtracker.data.dao

import androidx.room.*
import com.mselrod.billtracker.data.entity.PayDay
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for PayDay operations
 */
@Dao
interface PayDayDao {
    @Query("SELECT * FROM paydays ORDER BY day ASC")
    fun getAllPayDays(): Flow<List<PayDay>>

    @Query("SELECT * FROM paydays WHERE id = :id")
    suspend fun getPayDayById(id: Long): PayDay?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayDay(payDay: PayDay): Long

    @Update
    suspend fun updatePayDay(payDay: PayDay)

    @Delete
    suspend fun deletePayDay(payDay: PayDay)

    @Query("DELETE FROM paydays WHERE id = :id")
    suspend fun deletePayDayById(id: Long)

    @Query("DELETE FROM paydays")
    suspend fun deleteAllPayDays()
}
