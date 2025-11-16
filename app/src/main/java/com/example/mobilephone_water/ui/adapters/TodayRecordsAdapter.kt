package com.example.mobilephone_water.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilephone_water.R
import com.example.mobilephone_water.data.entity.WaterRecord
import java.text.SimpleDateFormat
import java.util.*

class TodayRecordsAdapter(
    private val records: List<WaterRecord>
) : RecyclerView.Adapter<TodayRecordsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

        fun bind(record: WaterRecord) {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            tvTime.text = timeFormat.format(Date(record.timestamp))
            tvAmount.text = "${record.amount} мл"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_water_record, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount() = records.size
}
