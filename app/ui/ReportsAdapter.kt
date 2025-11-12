// ReportsAdapter.kt
package com.potholereporter.ai.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.potholereporter.ai.R
import com.potholereporter.ai.data.PotholeReport
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RecyclerView Adapter for displaying Pothole Reports.
 */
class ReportsAdapter(
    private val context: Context,
    private val onItemClicked: (PotholeReport) -> Unit
) : ListAdapter<PotholeReport, ReportsAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = getItem(position)
        holder.bind(report)
        holder.itemView.setOnClickListener { onItemClicked(report) }
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
        private val severityText: TextView = itemView.findViewById(R.id.text_item_severity)
        private val locationText: TextView = itemView.findViewById(R.id.text_item_location)
        private val dateText: TextView = itemView.findViewById(R.id.text_item_date)

        fun bind(report: PotholeReport) {
            // Load the image from the saved file path using Coil
            val imageFile = File(report.imagePath)
            imageView.load(imageFile) {
                placeholder(R.drawable.ic_placeholder) // Use a placeholder icon if necessary
                error(R.drawable.ic_error) // Use an error icon
                crossfade(true)
            }

            // Format and display the severity score
            severityText.text = context.getString(R.string.severity_score_format_list, report.severityScore)

            // Display location (simplified for UI)
            val locationStr = "Lat: ${String.format("%.2f", report.latitude)}, Lon: ${String.format("%.2f", report.longitude)}"
            locationText.text = locationStr

            // Format and display the timestamp
            val date = Date(report.timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy (HH:mm)", Locale.US)
            dateText.text = format.format(date)
        }
    }

    // DiffUtil for efficient RecyclerView updates
    class ReportDiffCallback : DiffUtil.ItemCallback<PotholeReport>() {
        override fun areItemsTheSame(oldItem: PotholeReport, newItem: PotholeReport): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PotholeReport, newItem: PotholeReport): Boolean {
            return oldItem == newItem
        }
    }
}