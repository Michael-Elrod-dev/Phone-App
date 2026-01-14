package com.mselrod.billtracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mselrod.billtracker.data.dao.BillDao
import com.mselrod.billtracker.data.dao.PayDayDao
import com.mselrod.billtracker.data.entity.Bill
import com.mselrod.billtracker.data.entity.PayDay

/**
 * Room Database for Bill Tracker app
 */
@Database(
    entities = [Bill::class, PayDay::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao
    abstract fun payDayDao(): PayDayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bill_tracker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
