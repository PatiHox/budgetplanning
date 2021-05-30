package com.example.budgetplanning.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "bal_change")
data class BalanceChange(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "new_value") val newValue: Double,
    @ColumnInfo(name = "date_time") val dateTime: LocalDateTime
    )
