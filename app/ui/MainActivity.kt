// MainActivity.kt
package com.potholereporter.ai.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.potholereporter.ai.R
import com.potholereporter.ai.data.AppDatabase
import com.potholereporter.ai.data.FileManager
import com.potholereporter.ai.data.Repository
import com.potholereporter.ai.domain.CalculateSeverityUseCase
import com.potholereporter.ai.domain.FakeAiAnalyzer
import kotlinx.coroutines.launch

/**
 * The main screen of the Pothole Reporter AI app.
 * Handles photo capture/upload, analysis, and report saving.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var severityTextView: TextView
    private var selectedImageUri: Uri? = null

    // Initialize core components
    private val repository by lazy {
        val dao = AppDatabase.getDatabase(applicationContext).reportDao()
        Repository(dao)
    }
    private val aiAnalyzer = FakeAiAnalyzer()
    private val fileManager by lazy { FileManager(applicationContext) }
    private val calculateSeverityUseCase by lazy {
        CalculateSeverityUseCase(applicationContext, aiAnalyzer, fileManager)
    }

    // --- Activity Result Contracts for Permissions and Media ---

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraGranted && locationGranted) {
            // Permissions granted, can proceed with full functionality
            Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera/Location permissions are required for full functionality.", Toast.LENGTH_LONG).show()
        }
    }

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Toast.makeText(this, "Photo selected. Ready for analysis.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Lifecycle Methods ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        severityTextView = findViewById(R.id.text_severity)
        val captureButton: Button = findViewById(R.id.btn_capture_upload)
        val analyzeButton: Button = findViewById(R.id.btn_analyze)
        val viewReportsButton: Button = findViewById(R.id.btn_view_reports)

        // Request necessary permissions on start
        requestPermissions()

        captureButton.setOnClickListener {
            // Launch the system's image picker (handles both gallery and camera intent)
            selectImageLauncher.launch("image/*")
        }

        analyzeButton.setOnClickListener {
            analyzeAndSaveReport()
        }

        viewReportsButton.setOnClickListener {
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
        }
    }

    // --- Core Logic ---

    private fun requestPermissions() {
        // Request all required permissions at once
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE // For older APIs, though GetContent handles most
            )
        )
    }

    private fun analyzeAndSaveReport() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please capture or upload a photo first.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check for location permission before proceeding to get location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted. Cannot save GPS data.", Toast.LENGTH_LONG).show()
            return
        }

        // Update UI to show processing
        severityTextView.text = getString(R.string.severity_analyzing)

        lifecycleScope.launch {
            // Run the Use Case, which handles saving the image, getting location, and running fake AI
            val report = calculateSeverityUseCase(selectedImageUri!!)

            if (report != null) {
                // Report successfully created, now save it to the local database
                repository.insertReport(report)

                // Update UI with the final severity score
                severityTextView.text = getString(R.string.severity_score_format, report.severityScore)
                Toast.makeText(this@MainActivity, "Report saved! Severity: ${report.severityScore}", Toast.LENGTH_LONG).show()

            } else {
                severityTextView.text = getString(R.string.severity_default)
                Toast.makeText(this@MainActivity, "Failed to analyze or save report. Check permissions and try again.", Toast.LENGTH_LONG).show()
            }
        }
    }
}