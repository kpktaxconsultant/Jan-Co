package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tax_slabs",
    foreignKeys = [
        ForeignKey(
            entity = TaxYearEntity::class,
            parentColumns = ["id"],
            childColumns = ["taxYearId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taxYearId"])]
)
data class TaxSlabEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taxYearId: Long,
    val minIncome: Double,
    val maxIncome: Double, // Double.MAX_VALUE for "Above" slab
    val fixedTax: Double,
    val percentageRate: Double,
    val exemptionLimit: Double = 0.0,
    val slabOrder: Int = 0
)
