package com.example.data

import kotlinx.coroutines.flow.Flow

class JanTaxRepository(private val dao: JanTaxDao) {

    val allTaxYears: Flow<List<TaxYearEntity>> = dao.getAllTaxYears()
    val activeTaxYear: Flow<TaxYearEntity?> = dao.getActiveTaxYear()
    val allHistory: Flow<List<CalculationHistoryEntity>> = dao.getAllCalculationHistory()
    val allLeads: Flow<List<LeadEntity>> = dao.getAllLeads()

    fun searchHistory(query: String): Flow<List<CalculationHistoryEntity>> {
        return dao.searchCalculationHistory(query)
    }

    fun getSlabsForTaxYear(taxYearId: Long): Flow<List<TaxSlabEntity>> {
        return dao.getSlabsForTaxYear(taxYearId)
    }

    suspend fun getSlabsForTaxYearSync(taxYearId: Long): List<TaxSlabEntity> {
        return dao.getSlabsForTaxYearSync(taxYearId)
    }

    suspend fun getActiveTaxYearSync(): TaxYearEntity? {
        return dao.getActiveTaxYearSync()
    }

    suspend fun insertTaxYear(taxYear: TaxYearEntity): Long {
        return dao.insertTaxYear(taxYear)
    }

    suspend fun updateTaxYear(taxYear: TaxYearEntity) {
        dao.updateTaxYear(taxYear)
    }

    suspend fun deleteTaxYear(taxYearId: Long) {
        dao.deleteTaxYearById(taxYearId)
    }

    suspend fun setActiveTaxYear(taxYearId: Long) {
        dao.setActiveYear(taxYearId)
    }

    suspend fun saveSlabsForTaxYear(taxYearId: Long, slabs: List<TaxSlabEntity>) {
        dao.deleteSlabsForTaxYear(taxYearId)
        dao.insertTaxSlabs(slabs)
    }

    suspend fun saveCalculationHistory(history: CalculationHistoryEntity): Long {
        return dao.insertCalculationHistory(history)
    }

    suspend fun deleteCalculationHistory(id: Long) {
        dao.deleteCalculationHistoryById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }

    suspend fun saveLead(lead: LeadEntity): Long {
        return dao.insertLead(lead)
    }

    suspend fun deleteLead(id: Long) {
        dao.deleteLeadById(id)
    }
}
