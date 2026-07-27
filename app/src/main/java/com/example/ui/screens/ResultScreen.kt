package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.TaxCalculatorEngine
import com.example.service.PdfExportService
import com.example.service.WhatsAppService
import com.example.ui.theme.CorporateGold
import com.example.ui.theme.CorporateNavy
import com.example.ui.theme.CorporateNavyDark
import com.example.ui.viewmodels.MainViewModel
import com.example.ui.widgets.LeadFormModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val result by viewModel.currentResult.collectAsState()
    val showLeadDialog by viewModel.showLeadDialog.collectAsState()
    val lastLead by viewModel.lastSavedLead.collectAsState()

    var isSavedToHistory by remember { mutableStateOf(false) }

    if (showLeadDialog) {
        LeadFormModal(
            onDismiss = { viewModel.closeLeadDialog() },
            onSubmit = { name, mobile, email, city, occupation, purpose ->
                viewModel.submitLead(name, mobile, email, city, occupation, purpose) { savedLead ->
                    result?.let { currentRes ->
                        PdfExportService.generateAndSharePdf(context, currentRes, savedLead)
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tax Assessment Result",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CorporateNavy)
            )
        }
    ) { innerPadding ->
        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No calculation result available.")
            }
        } else {
            val res = result!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Result Hero Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(CorporateNavyDark, CorporateNavy, Color(0xFF0F172A))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = CorporateGold.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "TAX ASSESSMENT • FY ${res.taxYear}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                        color = CorporateGold
                                    )
                                }
                                Text(
                                    text = res.calculationDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            Text(
                                text = "Total Income Tax Payable",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Text(
                                text = "PKR ${TaxCalculatorEngine.formatCurrency(res.totalTax)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = CorporateGold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("Gross Annual Income", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text("PKR ${TaxCalculatorEngine.formatCurrency(res.annualIncome)}", style = MaterialTheme.typography.titleSmall, color = Color.White)
                                }

                                Column {
                                    Text("Effective Rate", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                                    Text("${String.format("%.2f", res.effectiveRate)}%", style = MaterialTheme.typography.titleSmall, color = CorporateGold)
                                }
                            }
                        }
                    }
                }

                // Detailed Tax Breakdown Table
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Detailed Calculation Breakdown",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HorizontalDivider()

                        BreakdownRow("Annual Business Income", "PKR ${TaxCalculatorEngine.formatCurrency(res.annualIncome)}")
                        BreakdownRow("Taxable Income Base", "PKR ${TaxCalculatorEngine.formatCurrency(res.taxableIncome)}")
                        BreakdownRow("Applicable Slab Range", res.slabDescription)
                        BreakdownRow("Fixed Tax Portion", "PKR ${TaxCalculatorEngine.formatCurrency(res.fixedTax)}")
                        BreakdownRow("Variable Tax Rate", "${res.percentageRate}%")
                        BreakdownRow("Variable Tax Portion", "PKR ${TaxCalculatorEngine.formatCurrency(res.variableTax)}")
                        BreakdownRow("Total Annual Tax", "PKR ${TaxCalculatorEngine.formatCurrency(res.totalTax)}", isHighlighted = true)
                        BreakdownRow("Effective Tax Rate", "${String.format("%.2f", res.effectiveRate)}%")
                        BreakdownRow("Assessment Year", res.taxYear)
                    }
                }

                // Professional Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = CorporateNavy, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Official Consultant's Assessment Summary",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = res.professionalSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action Buttons
                Button(
                    onClick = {
                        if (lastLead != null) {
                            PdfExportService.generateAndSharePdf(context, res, lastLead)
                        } else {
                            viewModel.openLeadDialog()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("download_pdf_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = CorporateGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Official PDF Report", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!isSavedToHistory) {
                                viewModel.saveCurrentCalculationToHistory()
                                isSavedToHistory = true
                                Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("save_history_button"),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isSavedToHistory) Icons.Default.Check else Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSavedToHistory) "Saved" else "Save History")
                    }

                    Button(
                        onClick = {
                            WhatsAppService.openWhatsAppChat(
                                context,
                                "Hello JAN & CO, I have calculated my Business Income Tax (Annual Income: PKR ${TaxCalculatorEngine.formatCurrency(res.annualIncome)}, Total Tax: PKR ${TaxCalculatorEngine.formatCurrency(res.totalTax)}). I would like formal tax filing advisory."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("whatsapp_consult_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp Advisory", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlighted) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.colorScheme.let { MaterialTheme.typography.bodyMedium },
            color = if (isHighlighted) CorporateNavy else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
            color = if (isHighlighted) CorporateGold else MaterialTheme.colorScheme.onSurface
        )
    }
}
