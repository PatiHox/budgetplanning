package com.example.budgetplanning.utils

import android.util.Log
import com.example.budgetplanning.data.entities.Transaction
import java.lang.Exception
import java.time.LocalDateTime
import java.time.Duration;

object ChartUtils {
    var minDateTime: LocalDateTime? = null

    fun initForDataArray(transactions: List<Transaction>){
        // Find min
        val minDate: LocalDateTime = transactions[0].dateTime
        for (t in transactions){
            if(minDate.isAfter(t.dateTime)){
                this.minDateTime = t.dateTime
            }
        }

        //

    }

    fun getXPosOfTransaction(transaction: Transaction): Float{
        if(minDateTime == null)
            throw Exception("Min date is not initialized!")
        if(transaction.dateTime.isBefore(minDateTime)){
            minDateTime = transaction.dateTime
            return 0.0f
        }

        val duration = Duration.between(minDateTime!!, transaction.dateTime)


        Log.d("CharUtils", "Converting long: ${duration.seconds} to float: ${duration.seconds.toString().toFloat()}")
        return duration.seconds.toString().toFloat()
    }

}