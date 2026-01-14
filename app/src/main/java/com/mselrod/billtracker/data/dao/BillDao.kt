package com.mselrod.billtracker.data.dao

import androidx.room.*
import com.mselrod.billtracker.data.entity.Bill
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Bill operations
 */
@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY name ASC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getBillById(id: Long): Bill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Update
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)

    @Query("DELETE FROM bills")
    suspend fun deleteAllBills()
}
