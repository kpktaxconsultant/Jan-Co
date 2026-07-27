package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TaxSlabEntity
import com.example.domain.TaxCalculatorEngine
import com.example.ui.theme.CorporateGold
import com.example.ui.theme.CorporateNavy
import com.example.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxGuideScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val allYears by viewModel.allTaxYears.collectAsState()
    val activeYear by viewModel.activeTaxYear.collectAsState()
    val selectedYearId by viewModel.selectedTaxYearId.collectAsState()
    val currentSlabs by viewModel.currentSlabs.collectAsState()

    var isYearMenuExpanded by remember { mutableStateOf(false) }

    val currentYearObj = remember(selectedYearId, activeYear, allYears) {
        allYears.find { it.id == selectedYearId } ?: activeYear ?: allYears.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FBR Income Tax Slabs Guide",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tax Year Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = isYearMenuExpanded,
                onExpandedChange = { isYearMenuExpanded = !isYearMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "Tax Year ${currentYearObj?.taxYear ?: "2024-2025"}${if (currentYearObj?.isActive == true) " (Active)" else ""}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tax Year Slabs Reference") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isYearMenuExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )

                ExposedDropdownMenu(
                    expanded = isYearMenuExpanded,
                    onDismissRequest = { isYearMenuExpanded = false }
                ) {
                    allYears.forEach { year ->
                        DropdownMenuItem(
                            text = { Text("Tax Year ${year.taxYear}${if (year.isActive) " • Active" else ""}") },
                            onClick = {
                                viewModel.setSelectedTaxYearId(year.id)
                                isYearMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Slabs Table
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Official Slabs Breakdown (${currentYearObj?.taxYear})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = CorporateNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CorporateNavy, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Taxable Income (PKR)", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text("Rate / Tax Calculation", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CorporateGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.heightIn(max = 380.dp)
                    ) {
                        items(currentSlabs) { slab ->
                            SlabRowItem(slab)
                        }
                    }
                }
            }

            // Explanatory Notes
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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Important Legal & Compliance Notes:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Slabs apply to Individuals, Sole Proprietors, and Unregistered Firms carrying on business in Pakistan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Effective dates for Tax Year ${currentYearObj?.taxYear} run from ${currentYearObj?.effectiveDate} to ${currentYearObj?.expiryDate}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• For formal FBR return filing, active tax credits, and corporate withholding compliance, consult JAN & CO (+92 327 7669933).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SlabRowItem(slab: TaxSlabEntity) {
    val rangeText = if (slab.maxIncome == Double.MAX_VALUE) {
        "Above ${TaxCalculatorEngine.formatCurrency(slab.minIncome - 1)}"
    } else {
        "${TaxCalculatorEngine.formatCurrency(slab.minIncome)} – ${TaxCalculatorEngine.formatCurrency(slab.maxIncome)}"
    }

    val taxFormulaText = if (slab.fixedTax == 0.0 && slab.percentageRate == 0.0) {
        "Tax = 0 (Exempt)"
    } else if (slab.fixedTax == 0.0) {
        "${slab.percentageRate}% of amount exceeding ${TaxCalculatorEngine.formatCurrency(slab.exemptionLimit)}"
    } else {
        "${TaxCalculatorEngine.formatCurrency(slab.fixedTax)} + ${slab.percentageRate}% exceeding ${TaxCalculatorEngine.formatCurrency(slab.exemptionLimit)}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rangeText,
            modifier = Modifier.weight(1.5f),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = taxFormulaText,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodySmall,
            color = CorporateNavy
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}
