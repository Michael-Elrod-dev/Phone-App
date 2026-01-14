package com.mselrod.billtracker.data.repository

import com.mselrod.billtracker.data.dao.BillDao
import com.mselrod.billtracker.data.dao.PayDayDao
import com.mselrod.billtracker.data.entity.Bill
import com.mselrod.billtracker.data.entity.PayDay
import kotlinx.coroutines.flow.Flow

/**
 * Repository layer for managing Bill and PayDay data operations
 */
class BillRepository(
    private val billDao: BillDao,
    private val payDayDao: PayDayDao
) {
    val allBills: Flow<List<Bill>> = billDao.getAllBills()

    suspend fun insertBill(bill: Bill): Long {
        return billDao.insertBill(bill)
    }

    suspend fun updateBill(bill: Bill) {
        billDao.updateBill(bill)
    }

    suspend fun deleteBill(bill: Bill) {
        billDao.deleteBill(bill)
    }

    val allPayDays: Flow<List<PayDay>> = payDayDao.getAllPayDays()

    suspend fun insertPayDay(payDay: PayDay): Long {
        return payDayDao.insertPayDay(payDay)
    }

    suspend fun updatePayDay(payDay: PayDay) {
        payDayDao.updatePayDay(payDay)
    }

    suspend fun deletePayDay(payDay: PayDay) {
        payDayDao.deletePayDay(payDay)
    }

    suspend fun deleteAllPayDays() {
        payDayDao.deleteAllPayDays()
    }
}
