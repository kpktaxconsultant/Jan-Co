package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taxYear: String,
    val annualIncome: Double,
    val taxableIncome: Double,
    val slabDescription: String,
    val fixedTax: Double,
    val variableTax: Double,
    val totalTax: Double,
    val effectiveRate: Double,
    val clientName: String = "",
    val clientCity: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
