package com.example.budgetplanning.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetplanning.data.AppDatabase
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.data.repositories.BalanceChangeRepository
import com.example.budgetplanning.data.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BalanceChangeViewModel(application: Application): AndroidViewModel(application) {
    val getAll: LiveData<List<BalanceChange>>
    val getLast: LiveData<BalanceChange>
    private val repository: BalanceChangeRepository

    init {
        val balanceChangeDao = AppDatabase.getInstance(application).balanceChangeDao()
        repository = BalanceChangeRepository(balanceChangeDao)
        getAll = repository.getAll
        getLast = repository.getLast
    }

    fun insertAll(vararg balanceChanges: BalanceChange){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(*balanceChanges)
        }
    }

    fun delete(balanceChange: BalanceChange){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(balanceChange)
        }
    }
}