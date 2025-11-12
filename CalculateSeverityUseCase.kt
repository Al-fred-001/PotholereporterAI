// CalculateSeverityUseCase.kt
package com.potholereporter.ai.domain

import android.content.Context
import android.net.Uri
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.potholereporter.ai.data.FileManager
import com.potholereporter.ai.data.PotholeReport
import kotlinx.coroutines.tasks.await

/**
 * Use case responsible for orchestrating the entire reporting process:
 * 1. Saves the image locally.
 * 2. Gets the current GPS location.
 * 3. Calls the AI Analyzer for a severity score.
 * 4. Assembles and returns a complete PotholeReport object.
 */
class CalculateSeverityUseCase(
    private val context: Context,
    private val aiAnalyzer: AiAnalyzer,
    private val fileManager: FileManager
) {
    // Client for location services
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Executes the report creation process.
     * @param imageUri The Uri of the image captured or uploaded by the user.
     * @return A PotholeReport object, or null if any step fails (e.g., permissions, saving, location).
     */
    suspend operator fun invoke(imageUri: Uri): PotholeReport? {
        // 1. Save the image to internal storage
        val imagePath = fileManager.saveImage(imageUri)
        if (imagePath == null) {
            return null
        }

        // 2. Get the current GPS location (Requires ACCESS_FINE_LOCATION permission)
        // NOTE: A real app should handle this more robustly, including checking permissions first.
        val location = try {
            fusedLocationClient.lastLocation.await()
        } catch (e: SecurityException) {
            // Permission not granted or location is off
            e.printStackTrace()
            null
        }

        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0

        // 3. Get the severity score from the AI analyzer
        // This is the primary hook for the real Firebender AI model.
        val severityScore = aiAnalyzer.analyze(imagePath)

        // 4. Assemble and return the report
        return PotholeReport(
            imagePath = imagePath,
            latitude = latitude,
            longitude = longitude,
            severityScore = severityScore,
            timestamp = System.currentTimeMillis()
        )
    }
}