package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.domain.TaxCalculationResult
import com.example.domain.TaxCalculatorEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = JanTaxDatabase.getDatabase(application, viewModelScope)
    val repository = JanTaxRepository(database.janTaxDao())

    // --- State Streams ---
    val allTaxYears: StateFlow<List<TaxYearEntity>> = repository.allTaxYears
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTaxYear: StateFlow<TaxYearEntity?> = repository.activeTaxYear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedTaxYearId = MutableStateFlow<Long?>(null)
    val selectedTaxYearId: StateFlow<Long?> = _selectedTaxYearId.asStateFlow()

    val currentSlabs: StateFlow<List<TaxSlabEntity>> = combine(
        selectedTaxYearId,
        activeTaxYear,
        repository.allTaxYears
    ) { selectedId, activeYear, allYears ->
        val targetId = selectedId ?: activeYear?.id ?: allYears.firstOrNull()?.id
        targetId
    }.flatMapLatest { id ->
        if (id != null) repository.getSlabsForTaxYear(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Calculator Input State ---
    private val _annualIncomeInput = MutableStateFlow("")
    val annualIncomeInput: StateFlow<String> = _annualIncomeInput.asStateFlow()

    private val _currentResult = MutableStateFlow<TaxCalculationResult?>(null)
    val currentResult: StateFlow<TaxCalculationResult?> = _currentResult.asStateFlow()

    // --- History State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val historyList: StateFlow<List<CalculationHistoryEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allHistory else repository.searchHistory(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Lead Generation State ---
    val allLeads: StateFlow<List<LeadEntity>> = repository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showLeadDialog = MutableStateFlow(false)
    val showLeadDialog: StateFlow<Boolean> = _showLeadDialog.asStateFlow()

    private val _lastSavedLead = MutableStateFlow<LeadEntity?>(null)
    val lastSavedLead: StateFlow<LeadEntity?> = _lastSavedLead.asStateFlow()

    // --- Admin Authentication State ---
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _adminPasswordInput = MutableStateFlow("")
    val adminPasswordInput: StateFlow<String> = _adminPasswordInput.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    // --- App Theme State ---
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // --- Calculator Actions ---
    fun onIncomeInputChanged(input: String) {
        // Strip out non-digit characters for numeric parsing
        val clean = input.filter { it.isDigit() }
        _annualIncomeInput.value = clean
    }

    fun setSelectedTaxYearId(id: Long) {
        _selectedTaxYearId.value = id
    }

    fun calculateTax() {
        val incomeVal = _annualIncomeInput.value.toDoubleOrNull() ?: 0.0
        val targetYear = allTaxYears.value.find { it.id == selectedTaxYearId.value }
            ?: activeTaxYear.value
            ?: allTaxYears.value.firstOrNull()

        if (targetYear != null) {
            val slabs = currentSlabs.value
            val res = TaxCalculatorEngine.calculateTax(incomeVal, targetYear, slabs)
            _currentResult.value = res
        }
    }

    fun resetCalculator() {
        _annualIncomeInput.value = ""
        _currentResult.value = null
    }

    fun saveCurrentCalculationToHistory(clientName: String = "", clientCity: String = "") {
        val result = currentResult.value ?: return
        viewModelScope.launch {
            repository.saveCalculationHistory(
                CalculationHistoryEntity(
                    taxYear = result.taxYear,
                    annualIncome = result.annualIncome,
                    taxableIncome = result.taxableIncome,
                    slabDescription = result.slabDescription,
                    fixedTax = result.fixedTax,
                    variableTax = result.variableTax,
                    totalTax = result.totalTax,
                    effectiveRate = result.effectiveRate,
                    clientName = clientName,
                    clientCity = clientCity
                )
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculationHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Lead Actions ---
    fun openLeadDialog() {
        _showLeadDialog.value = true
    }

    fun closeLeadDialog() {
        _showLeadDialog.value = false
    }

    fun submitLead(
        name: String,
        mobile: String,
        email: String,
        city: String,
        occupation: String,
        purpose: String,
        onSuccess: (LeadEntity) -> Unit
    ) {
        viewModelScope.launch {
            val lead = LeadEntity(
                name = name,
                mobile = mobile,
                email = email,
                city = city,
                occupation = occupation,
                purpose = purpose
            )
            val id = repository.saveLead(lead)
            val savedLead = lead.copy(id = id)
            _lastSavedLead.value = savedLead
            _showLeadDialog.value = false
            onSuccess(savedLead)
        }
    }

    fun deleteLead(id: Long) {
        viewModelScope.launch {
            repository.deleteLead(id)
        }
    }

    // --- Admin Actions ---
    fun onAdminPasswordChanged(password: String) {
        _adminPasswordInput.value = password
        _adminAuthError.value = null
    }

    fun authenticateAdmin(): Boolean {
        // Password is "ahmad10wali11"
        if (_adminPasswordInput.value == "ahmad10wali11") {
            _isAdminAuthenticated.value = true
            _adminAuthError.value = null
            _adminPasswordInput.value = ""
            return true
        } else {
            _adminAuthError.value = "Invalid Admin Password"
            return false
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _adminPasswordInput.value = ""
    }

    fun toggleActivateTaxYear(taxYearId: Long) {
        viewModelScope.launch {
            repository.setActiveTaxYear(taxYearId)
        }
    }

    fun deleteTaxYear(taxYearId: Long) {
        viewModelScope.launch {
            repository.deleteTaxYear(taxYearId)
        }
    }

    fun saveTaxYearWithSlabs(taxYear: TaxYearEntity, slabs: List<TaxSlabEntity>) {
        viewModelScope.launch {
            val yearId = repository.insertTaxYear(taxYear)
            val updatedSlabs = slabs.map { it.copy(taxYearId = yearId) }
            repository.saveSlabsForTaxYear(yearId, updatedSlabs)
            if (taxYear.isActive) {
                repository.setActiveTaxYear(yearId)
            }
        }
    }
}
