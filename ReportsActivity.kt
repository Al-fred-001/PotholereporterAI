// ReportsActivity.kt
package com.potholereporter.ai.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.potholereporter.ai.R
import com.potholereporter.ai.data.AppDatabase
import com.potholereporter.ai.data.Repository
import com.potholereporter.ai.ui.adapters.ReportsAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Activity to display a list of all submitted Pothole Reports.
 * It observes the local database for changes and updates the RecyclerView.
 */
class ReportsActivity : AppCompatActivity() {

    private lateinit var reportsAdapter: ReportsAdapter
    private val repository by lazy {
        val dao = AppDatabase.getDatabase(applicationContext).reportDao()
        Repository(dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Setup the Toolbar with a back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Past Reports"

        val recyclerView: RecyclerView = findViewById(R.id.recycler_reports)
        val fabBack: FloatingActionButton = findViewById(R.id.fab_back)

        // Initialize the adapter and set it to the RecyclerView
        reportsAdapter = ReportsAdapter(this) { report ->
            // Optional: Handle report item click (e.g., view details or confirm deletion)
            // For now, let's add a long-click listener to delete
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reportsAdapter

        // Collect reports from the database and update the adapter
        lifecycleScope.launch {
            // Using collectLatest to only process the most recent list
            repository.allReports.collectLatest { reports ->
                reportsAdapter.submitList(reports)
            }
        }

        // FAB to go back to the main activity (or add a new report)
        fabBack.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }
    }

    // Handle the back button press in the toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}