package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.LeadEntity
import com.example.data.TaxSlabEntity
import com.example.data.TaxYearEntity
import com.example.domain.TaxCalculatorEngine
import com.example.ui.theme.CorporateGold
import com.example.ui.theme.CorporateNavy
import com.example.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val passwordInput by viewModel.adminPasswordInput.collectAsState()
    val authError by viewModel.adminAuthError.collectAsState()

    val allYears by viewModel.allTaxYears.collectAsState()
    val allLeads by viewModel.allLeads.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Tax Years & Slabs, 1: Leads Database
    var showAddYearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Control Panel",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isAuthenticated) {
                        IconButton(onClick = { viewModel.logoutAdmin() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = CorporateGold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CorporateNavy)
            )
        }
    ) { innerPadding ->
        if (!isAuthenticated) {
            // Password Authentication Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = CorporateNavy
                        )

                        Text(
                            text = "Admin Password Protection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CorporateNavy
                        )

                        Text(
                            text = "Enter admin password to access the administrative control panel to manage tax slabs, active years, and consultation lead inquiries.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (authError != null) {
                            Text(
                                text = authError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { viewModel.onAdminPasswordChanged(it) },
                            label = { Text("Admin Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (viewModel.authenticateAdmin()) {
                                    Toast.makeText(context, "Admin Authenticated", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unlock Admin Panel", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Authenticated Admin Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Tax Years & Slabs") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Leads (${allLeads.size})") }
                    )
                }

                if (selectedTab == 0) {
                    // Tax Years Management
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Configured Tax Years",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Button(
                            onClick = { showAddYearDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CorporateNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Tax Year")
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(allYears, key = { it.id }) { yearEntity ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (yearEntity.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Tax Year ${yearEntity.taxYear}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Dates: ${yearEntity.effectiveDate} to ${yearEntity.expiryDate}",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (yearEntity.isActive) "ACTIVE" else "Inactive",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (yearEntity.isActive) CorporateNavy else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Switch(
                                                checked = yearEntity.isActive,
                                                onCheckedChange = {
                                                    viewModel.toggleActivateTaxYear(yearEntity.id)
                                                },
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }

                                    if (yearEntity.notes.isNotBlank()) {
                                        Text(
                                            text = "Notes: ${yearEntity.notes}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { viewModel.deleteTaxYear(yearEntity.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Leads Table
                    if (allLeads.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No leads recorded yet.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(allLeads, key = { it.id }) { lead ->
                                val dateStr = remember(lead.timestamp) {
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(lead.timestamp))
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = lead.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = CorporateNavy
                                            )
                                            Text(text = dateStr, style = MaterialTheme.typography.labelSmall)
                                        }

                                        Text(text = "Mobile: ${lead.mobile}  |  City: ${lead.city}", style = MaterialTheme.typography.bodySmall)
                                        if (lead.email.isNotBlank()) Text(text = "Email: ${lead.email}", style = MaterialTheme.typography.bodySmall)
                                        if (lead.occupation.isNotBlank()) Text(text = "Occupation: ${lead.occupation}", style = MaterialTheme.typography.bodySmall)
                                        Text(text = "Purpose: ${lead.purpose}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            IconButton(onClick = { viewModel.deleteLead(lead.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddYearDialog) {
        AddTaxYearDialog(
            onDismiss = { showAddYearDialog = false },
            onSave = { newYear, slabs ->
                viewModel.saveTaxYearWithSlabs(newYear, slabs)
                showAddYearDialog = false
            }
        )
    }
}

@Composable
private fun AddTaxYearDialog(
    onDismiss: () -> Unit,
    onSave: (TaxYearEntity, List<TaxSlabEntity>) -> Unit
) {
    var taxYearStr by remember { mutableStateOf("2025-2026") }
    var effectiveDate by remember { mutableStateOf("01-07-2025") }
    var expiryDate by remember { mutableStateOf("30-06-2026") }
    var notes by remember { mutableStateOf("Future Year Tax Rates") }
    var isActive by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add / Upload Tax Year") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = taxYearStr,
                    onValueChange = { taxYearStr = it },
                    label = { Text("Tax Year (e.g. 2025-2026)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = effectiveDate,
                    onValueChange = { effectiveDate = it },
                    label = { Text("Effective Date") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    singleLine = true
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Set as Active Tax Year")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val entity = TaxYearEntity(
                        taxYear = taxYearStr,
                        isActive = isActive,
                        effectiveDate = effectiveDate,
                        expiryDate = expiryDate,
                        notes = notes
                    )
                    // Pre-populate standard slabs as template
                    val templateSlabs = listOf(
                        TaxSlabEntity(taxYearId = 0, minIncome = 0.0, maxIncome = 600000.0, fixedTax = 0.0, percentageRate = 0.0, exemptionLimit = 600000.0, slabOrder = 1),
                        TaxSlabEntity(taxYearId = 0, minIncome = 600001.0, maxIncome = 1200000.0, fixedTax = 0.0, percentageRate = 15.0, exemptionLimit = 600000.0, slabOrder = 2),
                        TaxSlabEntity(taxYearId = 0, minIncome = 1200001.0, maxIncome = Double.MAX_VALUE, fixedTax = 90000.0, percentageRate = 20.0, exemptionLimit = 1200000.0, slabOrder = 3)
                    )
                    onSave(entity, templateSlabs)
                }
            ) {
                Text("Save Tax Year")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
