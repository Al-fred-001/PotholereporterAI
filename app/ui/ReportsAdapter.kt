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
 * Updated to display the new 0-100 Priority Score and detailed metrics.
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
        private val priorityText: TextView = itemView.findViewById(R.id.text_item_priority)
        private val sizeText: TextView = itemView.findViewById(R.id.text_item_size)
        private val trafficText: TextView = itemView.findViewById(R.id.text_item_traffic)
        private val dateText: TextView = itemView.findViewById(R.id.text_item_date)
        private val locationText: TextView = itemView.findViewById(R.id.text_item_location)


        fun bind(report: PotholeReport) {
            // Load the image from the saved file path using Coil
            val imageFile = File(report.imagePath)
            imageView.load(imageFile) {
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_error)
                crossfade(true)
            }

            // Display the new Final Priority Score (0-100)
            priorityText.text = String.format("%.0f", report.finalPriorityScore)

            // Display secondary ML metrics (from new data model fields)
            sizeText.text = context.getString(R.string.label_size_norm, String.format("%.2f", report.sizeNorm))
            trafficText.text = context.getString(R.string.label_traffic_rate, String.format("%.0f", report.trafficRate))

            // Format and display the timestamp and road type
            val date = Date(report.timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy (HH:mm)", Locale.US)
            val dateStr = context.getString(R.string.date_road_type_format, format.format(date), report.roadType)
            dateText.text = dateStr

            // Display location
            val locationStr = context.getString(R.string.location_format,
                String.format("%.2f", report.latitude),
                String.format("%.2f", report.longitude)
            )
            locationText.text = locationStr
        }
    }

    // DiffUtil for efficient RecyclerView updates
    class ReportDiffCallback : DiffUtil.ItemCallback<PotholeReport>() {
        override fun areItemsTheSame(oldItem: PotholeReport, newItem: PotholeReport): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PotholeReport, newItem: PotholeReport): Boolean {
            // Note: This needs to compare all relevant fields, now including new ones.
            return oldItem == newItem
        }
    }
}