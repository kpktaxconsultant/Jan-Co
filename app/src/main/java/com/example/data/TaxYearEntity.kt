package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax_years")
data class TaxYearEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taxYear: String, // e.g., "2024-2025"
    val isActive: Boolean = false,
    val minIncome: Double = 0.0,
    val maxIncome: Double = 0.0,
    val effectiveDate: String = "",
    val expiryDate: String = "",
    val notes: String = ""
)
