package com.example.budgetplanning.utils

import android.util.Log
import androidx.core.util.rangeTo
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.enums.Period
import com.example.budgetplanning.utils.ByteUtils.lastThreeBytesToFloat
import java.lang.Exception
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.Duration;
import kotlin.math.ceil
import kotlin.math.floor

object ChartUtils {
    /*var minDateTime: LocalDateTime? = null

    fun initForDataArray(transactions: List<Transaction>) {
        // Find min
        val minDate: LocalDateTime = transactions[0].dateTime
        for (t in transactions) {
            if (minDate.isAfter(t.dateTime)) {
                this.minDateTime = t.dateTime
            }
        }

        //

    }*/

    fun getXPosOfTransaction(
        firstTransaction: Transaction,
        statisticsPeriod: Period,
        transaction: Transaction
    ): Float {
        val duration = Duration.between(firstTransaction.dateTime, transaction.dateTime)

        return when (statisticsPeriod) {
            Period.DAY -> {
                duration.toDays().lastThreeBytesToFloat()
            }
            Period.THREE_DAYS -> {
                floor(duration.toDays().lastThreeBytesToFloat() / 3)
            }
            Period.WEEK -> {
                floor(duration.toDays().lastThreeBytesToFloat() / 7f)
            }
            Period.MONTH -> {
                (transaction.dateTime.year - firstTransaction.dateTime.year) * 12 + (transaction.dateTime.month.value - firstTransaction.dateTime.month.value).toFloat()
            }
            Period.THREE_MONTHS -> {
                floor((transaction.dateTime.year - firstTransaction.dateTime.year) * 12 + (transaction.dateTime.month.value - firstTransaction.dateTime.month.value).toFloat() / 3f)
            }
            Period.HALF_YEAR -> {
                floor((transaction.dateTime.year - firstTransaction.dateTime.year) * 12 + (transaction.dateTime.month.value - firstTransaction.dateTime.month.value).toFloat() / 6f)
            }
            Period.YEAR -> {
                (transaction.dateTime.year - firstTransaction.dateTime.year).toFloat()
            }
        }


/*        Log.d("CharUtils", "Converting long: ${duration.seconds} to float: ${duration.seconds.toString().toFloat()}")
        return duration.seconds.toString().toFloat()*/
    }

}