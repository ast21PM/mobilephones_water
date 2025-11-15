package com.example.mobilephone_water

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilephone_water.data.entity.WaterRecord
import com.example.mobilephone_water.ui.adapters.DayGroup
import com.example.mobilephone_water.ui.adapters.HistoryAdapter
import com.example.mobilephone_water.ui.viewmodel.WaterViewModel

class HistoryFragment : Fragment() {

    private lateinit var viewModel: WaterViewModel
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WaterViewModel::class.java]
        rvHistory = view.findViewById(R.id.rv_history)
        tvEmpty = view.findViewById(R.id.tv_empty)

        rvHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)


        viewModel.allRecords.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
                rvHistory.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
            } else {
                rvHistory.visibility = View.VISIBLE
                tvEmpty.visibility = View.GONE


                val groupedByDate = records.groupBy { it.date }
                    .toSortedMap(compareBy { it })
                    .entries
                    .map { DayGroup(it.key, it.value.sortedByDescending { r -> r.timestamp }) }
                    .reversed()

                rvHistory.adapter = HistoryAdapter(groupedByDate)
            }
        }
    }
}
