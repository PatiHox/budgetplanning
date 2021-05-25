package com.example.budgetplanning.utils

import com.example.budgetplanning.enums.Periods
import java.time.LocalDateTime

object DateUtils {
    fun getPeriodStartDateFromNow(period: Periods): LocalDateTime{
        val now = LocalDateTime.now()
        return when(period){
            Periods.DAY ->{
                now.minusDays(1)
            }
            Periods.THREE_DAYS ->{
                now.minusDays(3)
            }
            Periods.WEEK ->{
                now.minusWeeks(1)
            }
            Periods.THREE_MONTHS ->{
                now.minusMonths(3)
            }
            Periods.HALF_YEAR ->{
                now.minusMonths(6)
            }
            Periods.YEAR ->{
                now.minusYears(1)
            }
            else -> {
                now.minusMonths(1)
            }
        }
    }
}