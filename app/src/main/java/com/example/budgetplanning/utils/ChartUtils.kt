package com.example.budgetplanning.utils

import com.example.budgetplanning.enums.Period
import com.example.budgetplanning.utils.ByteUtils.lastThreeBytesToFloat
import java.time.Duration
import java.time.LocalDateTime
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

    fun getXPosOfDateTime(
        firstDateTime: LocalDateTime,
        statisticsPeriod: Period,
        dateTime: LocalDateTime
    ): Float {
        val duration = Duration.between(firstDateTime, dateTime)

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
                (dateTime.year - firstDateTime.year) * 12 + (dateTime.month.value - firstDateTime.month.value).toFloat()
            }
            Period.THREE_MONTHS -> {
                floor((dateTime.year - firstDateTime.year) * 12 + (dateTime.month.value - firstDateTime.month.value).toFloat() / 3f)
            }
            Period.HALF_YEAR -> {
                floor((dateTime.year - firstDateTime.year) * 12 + (dateTime.month.value - firstDateTime.month.value).toFloat() / 6f)
            }
            Period.YEAR -> {
                (dateTime.year - firstDateTime.year).toFloat()
            }
        }


/*        Log.d("CharUtils", "Converting long: ${duration.seconds} to float: ${duration.seconds.toString().toFloat()}")
        return duration.seconds.toString().toFloat()*/
    }

}