package com.mselrod.billtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Bill entity representing a recurring or one-time bill payment
 */
@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val day: Int? = null,
    val date: String? = null,
    val recurring: Boolean = true,
    val autopay: Boolean = false
)
