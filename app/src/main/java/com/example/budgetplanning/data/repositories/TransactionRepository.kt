package com.example.budgetplanning.data.repositories

import androidx.lifecycle.LiveData
import androidx.room.Delete
import com.example.budgetplanning.data.daos.TransactionDao
import com.example.budgetplanning.data.entities.Transaction

class TransactionRepository(private val transactionDao: TransactionDao) {
    val getAll: LiveData<List<Transaction>> = transactionDao.getAll()
    suspend fun getById(id: Int): Transaction {
        return transactionDao.getById(id)
    }

    suspend fun loadAllByIds(ids: IntArray): List<Transaction> {
        return transactionDao.loadAllByIds(ids)
    }

    suspend fun insertAll(vararg transactions: Transaction) {
        transactionDao.insertAll(*transactions)
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun update(transaction: Transaction){
        transactionDao.update(transaction)
    }
}