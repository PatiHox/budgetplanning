package com.example.budgetplanning.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transaction")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name="change_amount") val changeAmount: Float,
    @ColumnInfo(name="date_time") val dateTime: LocalDateTime = LocalDateTime.now(),
    val comment: String?
)