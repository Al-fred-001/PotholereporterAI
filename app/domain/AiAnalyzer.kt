// AiAnalyzer.kt
package com.potholereporter.ai.domain

/**
 * Interface defining the contract for the AI analysis module.
 * This is the hook for future integration with the real AI model (e.g., Firebender).
 */
interface AiAnalyzer {
    /**
     * Analyzes an image and returns a severity score.
     * @param imagePath The local file path of the image to analyze.
     * @return A Float representing the severity score (0.0 to 10.0).
     */
    suspend fun analyze(imagePath: String): Float
}