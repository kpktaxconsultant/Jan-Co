package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.LeadEntity
import com.example.domain.TaxCalculationResult
import com.example.domain.TaxCalculatorEngine
import java.io.File
import java.io.FileOutputStream

object PdfExportService {

    fun generateAndSharePdf(
        context: Context,
        result: TaxCalculationResult,
        lead: LeadEntity? = null
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size at 72 dpi (595x842)
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint()
            val primaryNavy = Color.parseColor("#0B1B3D")
            val primaryGold = Color.parseColor("#D4AF37")
            val darkText = Color.parseColor("#1E293B")
            val grayText = Color.parseColor("#64748B")
            val lightBg = Color.parseColor("#F8FAFC")
            val cardBg = Color.parseColor("#EFF6FF")

            // Top Header Navy Banner
            paint.color = primaryNavy
            canvas.drawRect(0f, 0f, 595f, 110f, paint)

            // Gold Accent Bar
            paint.color = primaryGold
            canvas.drawRect(0f, 106f, 595f, 110f, paint)

            // Brand Header Text
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 24f
            canvas.drawText("JAN & CO", 30f, 45f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = primaryGold
            canvas.drawText("Tax & Corporate Consultants", 30f, 65f, paint)

            paint.textSize = 10f
            paint.color = Color.WHITE
            canvas.drawText("Prepared by: TEHSIN ULLAH JAN • Advocate High Court", 30f, 85f, paint)

            // Report Title & Date
            paint.color = primaryNavy
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 16f
            canvas.drawText("OFFICIAL BUSINESS INCOME TAX ASSESSMENT REPORT", 30f, 145f, paint)

            paint.color = grayText
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Date: ${result.calculationDate}  |  Tax Year: ${result.taxYear}", 30f, 162f, paint)

            // Divider Line
            paint.color = Color.parseColor("#E2E8F0")
            paint.strokeWidth = 1f
            canvas.drawLine(30f, 175f, 565f, 175f, paint)

            var currentY = 195f

            // Client / Lead Info Card (if available)
            if (lead != null && lead.name.isNotBlank()) {
                paint.color = lightBg
                canvas.drawRoundRect(30f, currentY, 565f, currentY + 65f, 8f, 8f, paint)

                paint.color = primaryNavy
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 11f
                canvas.drawText("TAXPAYER / APPLICANT INFORMATION", 42f, currentY + 20f, paint)

                paint.color = darkText
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 10f
                canvas.drawText("Name: ${lead.name}  (${lead.occupation})", 42f, currentY + 38f, paint)
                canvas.drawText("Mobile: ${lead.mobile}  |  Email: ${lead.email}  |  City: ${lead.city}", 42f, currentY + 52f, paint)

                currentY += 80f
            }

            // Summary High-Level Metrics Box
            paint.color = cardBg
            canvas.drawRoundRect(30f, currentY, 565f, currentY + 75f, 8f, 8f, paint)

            paint.color = primaryNavy
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 10f
            canvas.drawText("ANNUAL BUSINESS INCOME", 45f, currentY + 25f, paint)
            canvas.drawText("TOTAL ESTIMATED TAX", 230f, currentY + 25f, paint)
            canvas.drawText("EFFECTIVE RATE", 430f, currentY + 25f, paint)

            paint.textSize = 14f
            canvas.drawText("PKR ${TaxCalculatorEngine.formatCurrency(result.annualIncome)}", 45f, currentY + 50f, paint)

            paint.color = primaryGold
            canvas.drawText("PKR ${TaxCalculatorEngine.formatCurrency(result.totalTax)}", 230f, currentY + 50f, paint)

            paint.color = primaryNavy
            canvas.drawText("${String.format("%.2f", result.effectiveRate)}%", 430f, currentY + 50f, paint)

            currentY += 95f

            // Detailed Tax Breakdown Table Header
            paint.color = primaryNavy
            canvas.drawRect(30f, currentY, 565f, currentY + 25f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 10f
            canvas.drawText("DESCRIPTION / CALCULATION PARAMETER", 40f, currentY + 16f, paint)
            canvas.drawText("AMOUNT / VALUE", 420f, currentY + 16f, paint)

            currentY += 25f

            // Table Rows
            val tableRows = listOf(
                "Tax Assessment Year" to result.taxYear,
                "Gross Annual Business Income" to "PKR ${TaxCalculatorEngine.formatCurrency(result.annualIncome)}",
                "Taxable Income Base" to "PKR ${TaxCalculatorEngine.formatCurrency(result.taxableIncome)}",
                "Applicable FBR Tax Slab" to result.slabDescription,
                "Fixed Tax Portion (Base Tax)" to "PKR ${TaxCalculatorEngine.formatCurrency(result.fixedTax)}",
                "Variable Tax Rate" to "${result.percentageRate}%",
                "Variable Tax Portion (Exceeding Amount)" to "PKR ${TaxCalculatorEngine.formatCurrency(result.variableTax)}",
                "Total Payable Income Tax" to "PKR ${TaxCalculatorEngine.formatCurrency(result.totalTax)}",
                "Effective Tax Burden Rate" to "${String.format("%.2f", result.effectiveRate)}%"
            )

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f

            for ((index, row) in tableRows.withIndex()) {
                val rowBg = if (index % 2 == 0) Color.WHITE else lightBg
                paint.color = rowBg
                canvas.drawRect(30f, currentY, 565f, currentY + 22f, paint)

                val isTotalRow = index == 7
                if (isTotalRow) {
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    paint.color = primaryNavy
                } else {
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    paint.color = darkText
                }

                canvas.drawText(row.first, 40f, currentY + 15f, paint)
                canvas.drawText(row.second, 420f, currentY + 15f, paint)

                paint.color = Color.parseColor("#CBD5E1")
                paint.strokeWidth = 0.5f
                canvas.drawLine(30f, currentY + 22f, 565f, currentY + 22f, paint)

                currentY += 22f
            }

            currentY += 20f

            // Professional Legal Summary Text Box
            paint.color = primaryNavy
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 11f
            canvas.drawText("EXECUTIVE LEGAL & TAX ADVISORY SUMMARY", 30f, currentY, paint)

            currentY += 15f
            paint.color = darkText
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 9.5f

            val summaryLines = wrapText(result.professionalSummary, 85)
            for (line in summaryLines) {
                canvas.drawText(line, 30f, currentY, paint)
                currentY += 14f
            }

            currentY += 25f

            // Legal Disclaimer & Signature Box
            paint.color = Color.parseColor("#E2E8F0")
            canvas.drawRoundRect(30f, currentY, 565f, currentY + 60f, 6f, 6f, paint)

            paint.color = grayText
            paint.textSize = 8.5f
            canvas.drawText("Consultancy Office: Bilal Market, Phase 1, Hayatabad, Peshawar.", 40f, currentY + 20f, paint)
            canvas.drawText("Contact: +92 327 7669933  |  Email: tehsinullahjan@gmail.com", 40f, currentY + 34f, paint)
            canvas.drawText("Disclaimer: This calculation is generated according to active FBR Pakistan Income Tax Ordinance provisions.", 40f, currentY + 48f, paint)

            // Footer Banner
            paint.color = primaryNavy
            canvas.drawRect(0f, 810f, 595f, 842f, paint)

            paint.color = primaryGold
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 9.5f
            canvas.drawText("Prepared using JAN & CO Business Tax Calculator", 30f, 828f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("TEHSIN ULLAH JAN • Advocate High Court", 350f, 828f, paint)

            pdfDocument.finishPage(page)

            // Write PDF to cache
            val fileName = "JAN_CO_Tax_Report_${System.currentTimeMillis()}.pdf"
            val cacheDir = File(context.cacheDir, "pdf_reports")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()

            // Share PDF Intent using FileProvider
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "JAN & CO Tax Assessment Report - ${result.taxYear}")
                putExtra(Intent.EXTRA_TEXT, "Please find attached the official Business Income Tax Assessment Report prepared by JAN & CO Tax & Corporate Consultants.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Tax Assessment PDF"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            if (currentLine.length + word.length + 1 <= maxChars) {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            } else {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }
}
