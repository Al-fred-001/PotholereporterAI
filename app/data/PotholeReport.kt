package com.potholereporter.ai.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a single Pothole Report entity for the Room database.
 * UPDATED to include fields for the full ML/Scoring model (even if currently zero/placeholder).
 */
@Entity(tableName = "pothole_reports")
data class PotholeReport(
    // Auto-generated primary key
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // File path where the photo is saved on internal storage (used for thumbnail)
    val imagePath: String,

    // GPS location data
    val latitude: Double,
    val longitude: Double,

    // --- Core Score (0-100) ---
    // This represents the final priority score from the sigmoid model.
    val finalPriorityScore: Float,

    // --- ML/CV Metrics (Placeholder for UI display) ---
    val sizeNorm: Float = 0f, // w1*size_norm
    val depthNorm: Float = 0f, // w2*depth_norm
    val edgeSharpnessNorm: Float = 0f, // w3*edge_sharpness_norm
    val trafficRate: Float = 0f, // w4*traffic_norm (vehicles counted per minute)

    // --- Contextual Tags/Weights (Placeholder for future UI tags) ---
    val nearSensitiveZone: Int = 0, // 0 or 1
    val roadType: String = "Local", // e.g., Highway, Arterial, Local

    // Timestamp when the report was created (in milliseconds)
    val timestamp: Long,

    // Placeholder for future use (e.g., duplicate detection)
    val visualHash: String = ""
)