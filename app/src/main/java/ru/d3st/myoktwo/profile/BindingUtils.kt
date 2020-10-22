package ru.d3st.myoktwo.profile

import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData

@BindingAdapter("setBarGraph")
fun BarChart.setBarGraph(item: BarData){
    val dataBarGraph = item
    data = dataBarGraph
}