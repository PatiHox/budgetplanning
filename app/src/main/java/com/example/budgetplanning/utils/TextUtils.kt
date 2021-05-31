package com.example.budgetplanning.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.budgetplanning.R
import java.util.*


object TextUtils {
    fun doubleToMoney(
        input: Double,
        context: Context,
        doAddPositiveSign: Boolean = true
    ): String {
        var money = context.resources.getString(
            R.string.money,
            String.format("%.2f", input),
            getMoneySymbol(context)
        )
        if (doAddPositiveSign && input > 0)
            money = "+$money"
        return money
    }

    fun getMoneySymbol(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        return when (prefs.getString("currency_symbol", "sys")) {
            "usd" -> {
                "$"
            }
            "uah" -> {
                "₴"
            }
            "rub" -> {
                "₽"
            }
            else -> {
                Currency.getInstance(Locale.getDefault()).symbol
            }
        }
    }
}