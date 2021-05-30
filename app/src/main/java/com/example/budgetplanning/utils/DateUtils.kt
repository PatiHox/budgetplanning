package com.example.budgetplanning.utils

import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.enums.Period
import java.time.LocalDateTime

object DateUtils {
    fun getPeriodStartDateFromNow(period: Period): LocalDateTime {
        val now = LocalDateTime.now()
        return when (period) {
            Period.DAY -> {
                now.minusDays(1)
            }
            Period.THREE_DAYS -> {
                now.minusDays(3)
            }
            Period.WEEK -> {
                now.minusWeeks(1)
            }
            Period.THREE_MONTHS -> {
                now.minusMonths(3)
            }
            Period.HALF_YEAR -> {
                now.minusMonths(6)
            }
            Period.YEAR -> {
                now.minusYears(1)
            }
            else -> {
                now.minusMonths(1)
            }
        }
    }

    fun getFirstTransaction(transactions: List<Transaction>): Transaction {
        var first = transactions[0]
        for (t in transactions) {
            if (t.dateTime.isBefore(first.dateTime))
                first = t
        }
        return first
    }
}