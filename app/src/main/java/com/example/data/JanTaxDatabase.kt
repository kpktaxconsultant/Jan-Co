package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaxYearEntity::class,
        TaxSlabEntity::class,
        CalculationHistoryEntity::class,
        LeadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JanTaxDatabase : RoomDatabase() {

    abstract fun janTaxDao(): JanTaxDao

    companion object {
        @Volatile
        private var INSTANCE: JanTaxDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JanTaxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JanTaxDatabase::class.java,
                    "jan_co_tax_db"
                )
                    .addCallback(JanTaxDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class JanTaxDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.janTaxDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: JanTaxDao) {
                // Preload Active Tax Year 2024-2025
                val activeYearId = dao.insertTaxYear(
                    TaxYearEntity(
                        taxYear = "2024-2025",
                        isActive = true,
                        minIncome = 0.0,
                        maxIncome = 100000000.0,
                        effectiveDate = "01-07-2024",
                        expiryDate = "30-06-2025",
                        notes = "Pakistan Federal Board of Revenue (FBR) Standard Business Income Tax Slabs for Individuals FY 2024-25"
                    )
                )

                // Preload Pakistan Standard Business Income Tax Slabs
                val slabsYear2024 = listOf(
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 0.0,
                        maxIncome = 600000.0,
                        fixedTax = 0.0,
                        percentageRate = 0.0,
                        exemptionLimit = 600000.0,
                        slabOrder = 1
                    ),
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 600001.0,
                        maxIncome = 1200000.0,
                        fixedTax = 0.0,
                        percentageRate = 15.0,
                        exemptionLimit = 600000.0,
                        slabOrder = 2
                    ),
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 1200001.0,
                        maxIncome = 1600000.0,
                        fixedTax = 90000.0,
                        percentageRate = 20.0,
                        exemptionLimit = 1200000.0,
                        slabOrder = 3
                    ),
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 1600001.0,
                        maxIncome = 3200000.0,
                        fixedTax = 170000.0,
                        percentageRate = 30.0,
                        exemptionLimit = 1600000.0,
                        slabOrder = 4
                    ),
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 3200001.0,
                        maxIncome = 5600000.0,
                        fixedTax = 650000.0,
                        percentageRate = 40.0,
                        exemptionLimit = 3200000.0,
                        slabOrder = 5
                    ),
                    TaxSlabEntity(
                        taxYearId = activeYearId,
                        minIncome = 5600001.0,
                        maxIncome = Double.MAX_VALUE,
                        fixedTax = 1610000.0,
                        percentageRate = 45.0,
                        exemptionLimit = 5600000.0,
                        slabOrder = 6
                    )
                )
                dao.insertTaxSlabs(slabsYear2024)

                // Preload Previous Tax Year 2023-2024 for Admin reference
                val prevYearId = dao.insertTaxYear(
                    TaxYearEntity(
                        taxYear = "2023-2024",
                        isActive = false,
                        minIncome = 0.0,
                        maxIncome = 100000000.0,
                        effectiveDate = "01-07-2023",
                        expiryDate = "30-06-2024",
                        notes = "Previous Year Pakistan Business Income Tax Rates for Individuals FY 2023-24"
                    )
                )
                val slabsYear2023 = listOf(
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 0.0,
                        maxIncome = 600000.0,
                        fixedTax = 0.0,
                        percentageRate = 0.0,
                        exemptionLimit = 600000.0,
                        slabOrder = 1
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 600001.0,
                        maxIncome = 800000.0,
                        fixedTax = 0.0,
                        percentageRate = 7.5,
                        exemptionLimit = 600000.0,
                        slabOrder = 2
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 800001.0,
                        maxIncome = 1200000.0,
                        fixedTax = 15000.0,
                        percentageRate = 15.0,
                        exemptionLimit = 800000.0,
                        slabOrder = 3
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 1200001.0,
                        maxIncome = 2400000.0,
                        fixedTax = 75000.0,
                        percentageRate = 20.0,
                        exemptionLimit = 1200000.0,
                        slabOrder = 4
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 2400001.0,
                        maxIncome = 3000000.0,
                        fixedTax = 315000.0,
                        percentageRate = 25.0,
                        exemptionLimit = 2400000.0,
                        slabOrder = 5
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 3000001.0,
                        maxIncome = 4000000.0,
                        fixedTax = 465000.0,
                        percentageRate = 30.0,
                        exemptionLimit = 3000000.0,
                        slabOrder = 6
                    ),
                    TaxSlabEntity(
                        taxYearId = prevYearId,
                        minIncome = 4000001.0,
                        maxIncome = Double.MAX_VALUE,
                        fixedTax = 765000.0,
                        percentageRate = 35.0,
                        exemptionLimit = 4000000.0,
                        slabOrder = 7
                    )
                )
                dao.insertTaxSlabs(slabsYear2023)
            }
        }
    }
}
