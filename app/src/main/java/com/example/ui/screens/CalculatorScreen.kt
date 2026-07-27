package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.TaxCalculatorEngine
import com.example.ui.theme.CorporateGold
import com.example.ui.theme.CorporateNavy
import com.example.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val allYears by viewModel.allTaxYears.collectAsState()
    val activeYear by viewModel.activeTaxYear.collectAsState()
    val selectedYearId by viewModel.selectedTaxYearId.collectAsState()
    val annualIncomeInput by viewModel.annualIncomeInput.collectAsState()
    val currentSlabs by viewModel.currentSlabs.collectAsState()

    var isDropdownExpanded by remember { mutableStateOf(false) }

    val currentYearObj = remember(selectedYearId, activeYear, allYears) {
        allYears.find { it.id == selectedYearId } ?: activeYear ?: allYears.firstOrNull()
    }

    val parsedIncome = remember(annualIncomeInput) {
        annualIncomeInput.toDoubleOrNull() ?: 0.0
    }

    val liveResult = remember(parsedIncome, currentYearObj, currentSlabs) {
        if (currentYearObj != null && currentSlabs.isNotEmpty()) {
            TaxCalculatorEngine.calculateTax(parsedIncome, currentYearObj, currentSlabs)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Business Tax Calculator",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "JAN & CO Tax Calculators • Module 01",
                            style = MaterialTheme.typography.labelSmall,
                            color = CorporateGold
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Pakistan Business Income Tax (Individuals)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your gross annual business income to calculate tax liability according to FBR slab rates.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Tax Year Selector Dropdown
            Text(
                text = "Select Tax Assessment Year",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "Tax Year ${currentYearObj?.taxYear ?: "2024-2025"}${if (currentYearObj?.isActive == true) " (Active)" else ""}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tax Year") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("tax_year_dropdown"),
                    shape = RoundedCornerShape(20.dp)
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    allYears.forEach { yearEntity ->
                        DropdownMenuItem(
                            text = {
                                Text("Tax Year ${yearEntity.taxYear}${if (yearEntity.isActive) " • Active" else ""}")
                            },
                            onClick = {
                                viewModel.setSelectedTaxYearId(yearEntity.id)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Annual Business Income Input
            Text(
                text = "Annual Business Income (PKR)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = annualIncomeInput,
                onValueChange = { viewModel.onIncomeInputChanged(it) },
                label = { Text("Enter Annual Income (e.g. 2,500,000)") },
                prefix = { Text("PKR ", fontWeight = FontWeight.Bold, color = CorporateNavy) },
                supportingText = {
                    if (parsedIncome > 0) {
                        Text("Formatted: PKR ${TaxCalculatorEngine.formatCurrency(parsedIncome)}")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("annual_income_input"),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            // Action Buttons (Calculate & Reset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetCalculator() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("reset_button"),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                }

                Button(
                    onClick = {
                        viewModel.calculateTax()
                        onNavigateToResult()
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(52.dp)
                        .testTag("calculate_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = CorporateGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Calculate Tax", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Breakdown Preview
            if (liveResult != null && parsedIncome > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Estimated Tax Breakdown Preview",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CorporateNavy
                        )
                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Applicable Slab:", style = MaterialTheme.typography.bodySmall)
                            Text(liveResult.slabDescription, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fixed Tax:", style = MaterialTheme.typography.bodySmall)
                            Text("PKR ${TaxCalculatorEngine.formatCurrency(liveResult.fixedTax)}", style = MaterialTheme.typography.bodySmall)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Variable Tax (${liveResult.percentageRate}%):", style = MaterialTheme.typography.bodySmall)
                            Text("PKR ${TaxCalculatorEngine.formatCurrency(liveResult.variableTax)}", style = MaterialTheme.typography.bodySmall)
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total Tax Payable:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = CorporateNavy
                            )
                            Text(
                                "PKR ${TaxCalculatorEngine.formatCurrency(liveResult.totalTax)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = CorporateGold
                            )
                        }
                    }
                }
            }
        }
    }
}
