package com.example.budgetplanning.utils

import android.content.res.Resources
import android.util.Log
import android.widget.EditText
import com.example.budgetplanning.R
import java.util.*


object TextUtils {
    fun floatToMoney(
        input: Float,
        resources: Resources,
        doAddPositiveSign: Boolean = true
    ): String {
        var money = resources.getString(
            R.string.money,
            String.format("%.2f", input),
            Currency.getInstance(Locale.getDefault()).symbol
        )
        if (doAddPositiveSign && input > 0)
            money = "+$money"
        return money
    }

    fun getMoneySymbol(): String{
        return Currency.getInstance(Locale.getDefault()).symbol
    }
}