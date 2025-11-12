// Repository.kt
package com.potholereporter.ai.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that handles data operations.
 * It provides a clean API to the rest of the app for accessing data.
 */
class Repository(private val reportDao: ReportDao) {

    // Flow to get all reports, ordered by severity
    val allReports: Flow<List<PotholeReport>> = reportDao.getAllReports()

    /**
     * Inserts a new PotholeReport.
     * @param report The report to insert.
     */
    suspend fun insertReport(report: PotholeReport) {
        reportDao.insert(report)
    }

    /**
     * Deletes a PotholeReport.
     * @param report The report to delete.
     */
    suspend fun deleteReport(report: PotholeReport) {
        reportDao.delete(report)
    }
}