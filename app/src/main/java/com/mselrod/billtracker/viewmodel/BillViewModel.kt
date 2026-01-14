package com.mselrod.billtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mselrod.billtracker.data.entity.Bill
import com.mselrod.billtracker.data.entity.PayDay
import com.mselrod.billtracker.data.repository.BillRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel for managing Bill Tracker app state and business logic
 */
class BillViewModel(private val repository: BillRepository) : ViewModel() {

    // State flows for UI data
    val bills: StateFlow<List<Bill>> = repository.allBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payDays: StateFlow<List<PayDay>> = repository.allPayDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _selectedDay = MutableStateFlow<LocalDate?>(null)

    private val _undoDeletedBill = MutableStateFlow<Bill?>(null)
    val undoDeletedBill: StateFlow<Bill?> = _undoDeletedBill.asStateFlow()

    // Bill operations
    fun addBill(
        name: String,
        amount: Double,
        day: Int? = null,
        date: String? = null,
        recurring: Boolean = true,
        autopay: Boolean = false
    ) {
        viewModelScope.launch {
            val bill = Bill(
                name = name,
                amount = amount,
                day = day,
                date = date,
                recurring = recurring,
                autopay = autopay
            )
            repository.insertBill(bill)
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            repository.updateBill(bill)
        }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            repository.deleteBill(bill)
            _undoDeletedBill.value = bill
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            _undoDeletedBill.value?.let { bill ->
                repository.insertBill(bill.copy(id = 0)) // Insert as new
                _undoDeletedBill.value = null
            }
        }
    }

    fun clearUndo() {
        _undoDeletedBill.value = null
    }

    fun addPayDay(day: Int, amount: Double) {
        viewModelScope.launch {
            repository.insertPayDay(PayDay(day = day, amount = amount))
        }
    }

    fun deletePayDay(payDay: PayDay) {
        viewModelScope.launch {
            repository.deletePayDay(payDay)
        }
    }

    fun setSelectedDay(date: LocalDate?) {
        _selectedDay.value = date
    }

    fun navigateNextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun navigatePreviousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    /**
     * Get the next payday date after today
     */
    private fun getNextPayDay(afterDate: LocalDate = LocalDate.now()): LocalDate? {
        val payDaysList = payDays.value
        if (payDaysList.isEmpty()) return null

        for (monthOffset in 0..2) {
            val checkMonth = afterDate.plusMonths(monthOffset.toLong())
            val daysInMonth = YearMonth.of(checkMonth.year, checkMonth.month).lengthOfMonth()

            payDaysList.forEach { payDay ->
                val actualDay = if (payDay.day > daysInMonth) daysInMonth else payDay.day
                val payDayDate = LocalDate.of(checkMonth.year, checkMonth.month, actualDay)

                if (payDayDate.isAfter(afterDate)) {
                    return payDayDate
                }
            }
        }

        return null
    }

    /**
     * Calculate total bills due before the next payday
     */
    fun calculateBillsBeforeNextPayDay(): Double {
        val nextPayDay = getNextPayDay() ?: return 0.0
        val today = LocalDate.now()

        return bills.value.filter { bill ->
            val billDate = when {
                bill.day != null -> {
                    val currentMonth = YearMonth.now()
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val actualDay = if (bill.day > daysInMonth) daysInMonth else bill.day
                    LocalDate.of(currentMonth.year, currentMonth.month, actualDay)
                }
                bill.date != null -> LocalDate.parse(bill.date)
                else -> null
            }

            billDate != null &&
            (billDate.isEqual(today) || billDate.isAfter(today)) &&
            billDate.isBefore(nextPayDay)
        }.sumOf { it.amount }
    }

    /**
     * Calculate total bills due before the next 2 paydays
     */
    fun calculateBillsBeforeNext2PayDays(): Double {
        val firstPayDay = getNextPayDay() ?: return 0.0
        val secondPayDay = getNextPayDay(firstPayDay) ?: return calculateBillsBeforeNextPayDay()
        val today = LocalDate.now()

        return bills.value.filter { bill ->
            val billDate = when {
                bill.day != null -> {
                    val currentMonth = YearMonth.now()
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val actualDay = if (bill.day > daysInMonth) daysInMonth else bill.day
                    var date = LocalDate.of(currentMonth.year, currentMonth.month, actualDay)

                    if (date.isBefore(today)) {
                        val nextMonth = currentMonth.plusMonths(1)
                        val nextMonthDays = nextMonth.lengthOfMonth()
                        val nextActualDay = if (bill.day > nextMonthDays) nextMonthDays else bill.day
                        date = LocalDate.of(nextMonth.year, nextMonth.month, nextActualDay)
                    }

                    date
                }
                bill.date != null -> LocalDate.parse(bill.date)
                else -> null
            }

            billDate != null &&
            (billDate.isEqual(today) || billDate.isAfter(today)) &&
            billDate.isBefore(secondPayDay)
        }.sumOf { it.amount }
    }
}
