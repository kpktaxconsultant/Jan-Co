package com.example.domain

import com.example.data.TaxSlabEntity
import com.example.data.TaxYearEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TaxCalculatorEngine {

    fun calculateTax(
        annualIncome: Double,
        taxYear: TaxYearEntity,
        slabs: List<TaxSlabEntity>
    ): TaxCalculationResult {
        val income = if (annualIncome < 0) 0.0 else annualIncome
        val formattedDate = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        if (slabs.isEmpty()) {
            return TaxCalculationResult(
                annualIncome = income,
                taxableIncome = income,
                slabDescription = "No slab configured for ${taxYear.taxYear}",
                minIncome = 0.0,
                maxIncome = 0.0,
                fixedTax = 0.0,
                percentageRate = 0.0,
                variableTax = 0.0,
                totalTax = 0.0,
                effectiveRate = 0.0,
                taxYear = taxYear.taxYear,
                calculationDate = formattedDate,
                professionalSummary = "No tax slabs available for Tax Year ${taxYear.taxYear}."
            )
        }

        // Find applicable slab based on income range
        val matchingSlab = slabs.find { slab ->
            income >= slab.minIncome && (income <= slab.maxIncome || slab.maxIncome == Double.MAX_VALUE)
        } ?: slabs.last()

        val exemptionLimit = matchingSlab.exemptionLimit
        val exceedingAmount = (income - exemptionLimit).coerceAtLeast(0.0)
        val variableTax = exceedingAmount * (matchingSlab.percentageRate / 100.0)
        val totalTax = matchingSlab.fixedTax + variableTax
        val effectiveRate = if (income > 0) (totalTax / income) * 100.0 else 0.0

        val formattedIncome = formatCurrency(income)
        val formattedTotalTax = formatCurrency(totalTax)
        val formattedFixedTax = formatCurrency(matchingSlab.fixedTax)
        val formattedVariableTax = formatCurrency(variableTax)

        val slabDescription = if (matchingSlab.maxIncome == Double.MAX_VALUE) {
            "Above ${formatCurrency(matchingSlab.minIncome - 1)}"
        } else if (matchingSlab.fixedTax == 0.0 && matchingSlab.percentageRate == 0.0) {
            "0 – ${formatCurrency(matchingSlab.maxIncome)} (Exempt)"
        } else {
            "${formatCurrency(matchingSlab.minIncome)} – ${formatCurrency(matchingSlab.maxIncome)}"
        }

        val summary = buildString {
            append("Official Tax Estimate for Tax Year ${taxYear.taxYear} prepared by JAN & CO Tax Consultants.\n")
            if (totalTax == 0.0) {
                append("Your annual business income of PKR $formattedIncome falls within the tax-exempt threshold ($slabDescription). No income tax is payable.")
            } else {
                append("For an annual business income of PKR $formattedIncome, your tax liability falls under slab ($slabDescription). ")
                if (matchingSlab.fixedTax > 0) {
                    append("This consists of a fixed tax of PKR $formattedFixedTax plus ")
                }
                append("${matchingSlab.percentageRate}% tax on amount exceeding PKR ${formatCurrency(exemptionLimit)} (PKR $formattedVariableTax), resulting in a total annual income tax of PKR $formattedTotalTax (Effective Tax Rate: ${String.format(Locale.US, "%.2f", effectiveRate)}%).")
            }
        }

        return TaxCalculationResult(
            annualIncome = income,
            taxableIncome = income,
            slabDescription = slabDescription,
            minIncome = matchingSlab.minIncome,
            maxIncome = matchingSlab.maxIncome,
            fixedTax = matchingSlab.fixedTax,
            percentageRate = matchingSlab.percentageRate,
            variableTax = variableTax,
            totalTax = totalTax,
            effectiveRate = effectiveRate,
            taxYear = taxYear.taxYear,
            calculationDate = formattedDate,
            professionalSummary = summary
        )
    }

    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "PK"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }
}
