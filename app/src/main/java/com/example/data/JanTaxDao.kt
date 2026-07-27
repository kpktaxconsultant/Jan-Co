package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JanTaxDao {

    // --- Tax Years ---
    @Query("SELECT * FROM tax_years ORDER BY id DESC")
    fun getAllTaxYears(): Flow<List<TaxYearEntity>>

    @Query("SELECT * FROM tax_years WHERE isActive = 1 LIMIT 1")
    fun getActiveTaxYear(): Flow<TaxYearEntity?>

    @Query("SELECT * FROM tax_years WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveTaxYearSync(): TaxYearEntity?

    @Query("SELECT * FROM tax_years WHERE id = :id")
    suspend fun getTaxYearById(id: Long): TaxYearEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxYear(taxYear: TaxYearEntity): Long

    @Update
    suspend fun updateTaxYear(taxYear: TaxYearEntity)

    @Query("DELETE FROM tax_years WHERE id = :id")
    suspend fun deleteTaxYearById(id: Long)

    @Query("UPDATE tax_years SET isActive = 0")
    suspend fun deactivateAllTaxYears()

    @Query("UPDATE tax_years SET isActive = 1 WHERE id = :id")
    suspend fun activateTaxYearById(id: Long)

    @Transaction
    suspend fun setActiveYear(id: Long) {
        deactivateAllTaxYears()
        activateTaxYearById(id)
    }

    // --- Tax Slabs ---
    @Query("SELECT * FROM tax_slabs WHERE taxYearId = :taxYearId ORDER BY minIncome ASC")
    fun getSlabsForTaxYear(taxYearId: Long): Flow<List<TaxSlabEntity>>

    @Query("SELECT * FROM tax_slabs WHERE taxYearId = :taxYearId ORDER BY minIncome ASC")
    suspend fun getSlabsForTaxYearSync(taxYearId: Long): List<TaxSlabEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxSlab(slab: TaxSlabEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxSlabs(slabs: List<TaxSlabEntity>)

    @Query("DELETE FROM tax_slabs WHERE taxYearId = :taxYearId")
    suspend fun deleteSlabsForTaxYear(taxYearId: Long)

    // --- Calculation History ---
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllCalculationHistory(): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE clientName LIKE '%' || :query || '%' OR taxYear LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchCalculationHistory(query: String): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE id = :id")
    suspend fun getCalculationHistoryById(id: Long): CalculationHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculationHistory(history: CalculationHistoryEntity): Long

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteCalculationHistoryById(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()

    // --- Leads ---
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Long)
}
