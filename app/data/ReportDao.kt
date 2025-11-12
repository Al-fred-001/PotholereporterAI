// ReportDao.kt
package com.potholereporter.ai.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the PotholeReport entity.
 * Provides methods for interacting with the database.
 */
@Dao
interface ReportDao {

    /**
     * Inserts a new report into the database.
     * @param report The PotholeReport object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: PotholeReport)

    /**
     * Deletes a report from the database.
     * @param report The PotholeReport object to delete.
     */
    @Delete
    suspend fun delete(report: PotholeReport)

    /**
     * Retrieves all reports from the database.
     * The results are ordered by severityScore in descending order (highest score first).
     * @return A Flow of a List of PotholeReport objects. Flow allows observing changes.
     */
    @Query("SELECT * FROM pothole_reports ORDER BY severityScore DESC")
    fun getAllReports(): Flow<List<PotholeReport>>
}