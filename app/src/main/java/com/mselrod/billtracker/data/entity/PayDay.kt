package com.mselrod.billtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PayDay entity representing income received on specific days of the month
 */
@Entity(tableName = "paydays")
data class PayDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val day: Int,
    val amount: Double
)
