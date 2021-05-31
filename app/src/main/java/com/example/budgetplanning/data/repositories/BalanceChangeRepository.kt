package com.example.budgetplanning.data.repositories

import androidx.lifecycle.LiveData
import com.example.budgetplanning.data.daos.BalanceChangeDao
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction

class BalanceChangeRepository(private val balanceChangeDao: BalanceChangeDao) {
    val getAll: LiveData<List<BalanceChange>> = balanceChangeDao.getAll()
    val getLast: LiveData<BalanceChange> = balanceChangeDao.getLast()

    suspend fun insertAll(vararg balanceChanges: BalanceChange) {
        balanceChangeDao.insertAll(*balanceChanges)
    }

    suspend fun delete(balanceChange: BalanceChange) {
        balanceChangeDao.delete(balanceChange)
    }

    suspend fun update(balanceChange: BalanceChange){
        balanceChangeDao.update(balanceChange)
    }
}