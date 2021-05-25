package com.example.budgetplanning.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetplanning.data.AppDatabase
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.data.repositories.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    val getAll: LiveData<List<Transaction>>
    private val repository: TransactionRepository

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        getAll = repository.getAll
    }

    fun insertAll(vararg transactions: Transaction){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(*transactions)
        }
    }

    fun delete(transaction: Transaction){
        viewModelScope.launch(Dispatchers.IO){
            repository.delete(transaction)
        }
    }

    fun update(transaction: Transaction){
        viewModelScope.launch(Dispatchers.IO){
            repository.update(transaction)
        }
    }

    // Не думаю что это будет работать
    /*fun getById(id: Int): Transaction{
        var res: Transaction
        viewModelScope.launch(Dispatchers.IO) {
            res = repository.getById(id)
        }
        return res
    }*/
}