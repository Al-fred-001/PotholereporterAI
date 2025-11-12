// FakeAiAnalyzer.kt
package com.potholereporter.ai.domain

import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * A placeholder implementation of AiAnalyzer.
 * It simulates an AI analysis by returning a random score and adding a delay.
 *
 * NOTE: Replace this class with the real implementation that connects to the
 * Firebender/ML model API once it's ready!
 */
class FakeAiAnalyzer : AiAnalyzer {
    override suspend fun analyze(imagePath: String): Float {
        // Simulate network/model latency
        delay(1500)

        // Generate a random float between 0.0 and 10.0
        val randomScore = Random.nextFloat() * 10f

        // Round to two decimal places
        return String.format("%.1f", randomScore).toFloat()
    }
}