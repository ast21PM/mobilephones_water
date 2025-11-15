package com.example.mobilephone_water.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilephone_water.R
import com.example.mobilephone_water.data.entity.WaterRecord
import java.text.SimpleDateFormat
import java.util.*

data class DayGroup(
    val date: String,
    val records: List<WaterRecord>
)

class HistoryAdapter(private val dayGroups: List<DayGroup>) :
    RecyclerView.Adapter<HistoryAdapter.DayViewHolder>() {


    private fun getCountText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "приём"      // 1, 21, 31, 41...
            count % 10 in 2..4 && count % 100 !in 12..14 -> "приёма" // 2-4, 22-24, 32-34...
            else -> "приёмов"                                      // 5-20, 25-30, 35-40...
        }
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayHeader: TextView = itemView.findViewById(R.id.tv_day_header)
        private val tvDaySummary: TextView = itemView.findViewById(R.id.tv_day_summary)
        private val tvExpandIcon: TextView = itemView.findViewById(R.id.tv_expand_icon)
        private val rvRecords: RecyclerView = itemView.findViewById(R.id.rv_records)
        private var isExpanded = false

        fun bind(dayGroup: DayGroup) {
            try {

                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dayGroup.date)


                val outputFormat = SimpleDateFormat("dd.MM.yyyy (EEEE)", Locale.getDefault())
                tvDayHeader.text = date?.let { outputFormat.format(it) } ?: dayGroup.date
            } catch (e: Exception) {

                tvDayHeader.text = dayGroup.date
            }


            val totalAmount = dayGroup.records.sumOf { it.amount }
            val count = dayGroup.records.size
            val countText = getCountText(count)

            tvDaySummary.text = "Всего: $totalAmount мл · $count $countText"


            rvRecords.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvRecords.adapter = RecordsAdapter(dayGroup.records)


            itemView.setOnClickListener {
                isExpanded = !isExpanded
                rvRecords.visibility = if (isExpanded) View.VISIBLE else View.GONE
                tvExpandIcon.text = if (isExpanded) "▲" else "▼"
            }
        }
    }

    inner class RecordsAdapter(private val records: List<WaterRecord>) :
        RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

        inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
            private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

            fun bind(record: WaterRecord) {

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = Date(record.timestamp)
                tvTime.text = timeFormat.format(time)


                tvAmount.text = "${record.amount} мл"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
            return RecordViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_water_record, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
            holder.bind(records[position])
        }

        override fun getItemCount() = records.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history_day, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(dayGroups[position])
    }

    override fun getItemCount() = dayGroups.size
}
