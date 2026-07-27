package com.example.domain

data class TaxCalculationResult(
    val annualIncome: Double,
    val taxableIncome: Double,
    val slabDescription: String,
    val minIncome: Double,
    val maxIncome: Double,
    val fixedTax: Double,
    val percentageRate: Double,
    val variableTax: Double,
    val totalTax: Double,
    val effectiveRate: Double, // Total Tax / Annual Income * 100
    val taxYear: String,
    val calculationDate: String,
    val professionalSummary: String
)
