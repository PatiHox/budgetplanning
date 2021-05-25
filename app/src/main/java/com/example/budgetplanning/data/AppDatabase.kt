package com.example.budgetplanning.data

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetplanning.R
import com.example.budgetplanning.data.daos.BalanceChangeDao
import com.example.budgetplanning.data.daos.TransactionDao
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.utils.DateConverter

@Database(entities = [Transaction::class, BalanceChange::class], version = 5, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun balanceChangeDao(): BalanceChangeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(R.string.dbname)
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}