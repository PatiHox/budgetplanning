package com.example.budgetplanning.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.budgetplanning.data.entities.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` ORDER BY id DESC")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE id =(:id)")
    suspend fun getById(id: Int): Transaction

    @Query("SELECT * FROM `transaction` WHERE id IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): List<Transaction>

//    @Query("SELECT * FROM `transaction` WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): TransactionEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg transactions: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}
