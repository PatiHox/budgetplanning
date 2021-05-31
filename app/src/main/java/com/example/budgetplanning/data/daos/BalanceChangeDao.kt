package com.example.budgetplanning.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetplanning.data.entities.BalanceChange

@Dao
interface BalanceChangeDao {
    @Query("SELECT * FROM `bal_change` ORDER BY id DESC")
    fun getAll(): LiveData<List<BalanceChange>>

    @Query("SELECT * FROM `bal_change` ORDER BY id DESC LIMIT 1")
    fun getLast(): LiveData<BalanceChange>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg balanceChanges: BalanceChange)

    @Delete
    suspend fun delete(balanceChange: BalanceChange)

    @Update
    suspend fun update(balanceChange: BalanceChange)
}