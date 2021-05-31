package com.example.budgetplanning.utils

import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction

data class MyEntryHelper(val x: Float) {
    val transactions: MutableList<Transaction> = mutableListOf()
    var lastBalanceChange: BalanceChange? = null
}