package com.dicoding.asclepius.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.PredictionHistoryEntity
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private var historyList: List<PredictionHistoryEntity>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    fun updateData(newHistoryList: List<PredictionHistoryEntity>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prediction_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
    }
    override fun getItemCount(): Int = historyList.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val historyImageView: ImageView = itemView.findViewById(R.id.history_image_view)
        private val labelTextView: TextView = itemView.findViewById(R.id.label_text_view)
        private val confidenceTextView: TextView = itemView.findViewById(R.id.confidence_text_view)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestamp_text_view)

        fun bind(historyItem: PredictionHistoryEntity) {
            historyImageView.setImageURI(Uri.parse(historyItem.imagePath))
            labelTextView.text = historyItem.predictionResult
            confidenceTextView.text = String.format("Confidence: %.2f%%", historyItem.confidenceScore * 100)
            timestampTextView.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(historyItem.timestamp))
        }
    }
}