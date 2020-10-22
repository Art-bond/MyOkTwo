package ru.d3st.myoktwo.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginRight
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.renderer.XAxisRenderer
import ru.d3st.myoktwo.databinding.DetailFragmentBinding
import ru.d3st.myoktwo.network.MyGroup
import ru.d3st.myoktwo.profile.setBarGraph

class Detail : Fragment() {

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val bind = DetailFragmentBinding.inflate(inflater, container, false)
        //через фрагмент получаем доступ к приложению
        val application = requireNotNull(activity).application
        //получаем данные из предыдущего фрагмента
        val selectedMyGroup = DetailArgs.fromBundle(arguments!!).selectedGroupId
        //создаем экземпляр ViewModelFactory, для того чтобы поместить данные из предыдущего фрагмента в ВьюМодел этого фрагмента
        val viewModelFactory = DetailViewModelFactory(selectedMyGroup, application)
        //биндим ВМ
        bind.detailDataViewModel =
            ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        //для обновления экрана
        bind.lifecycleOwner = this


        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel.barGraph.observe(viewLifecycleOwner, {
            Log.i("jsonRefresult", it.toString())
            it?.let {
                bind.barChartRef.data = it
                val mChart = bind.barChartRef

                mChart.setTouchEnabled(true)
                mChart.isClickable = false
                mChart.isDoubleTapToZoomEnabled = false
                mChart.isDoubleTapToZoomEnabled = false

                mChart.setDrawBorders(false)
                mChart.setDrawGridBackground(false)

                mChart.setDescription(null)
                mChart.legend.isEnabled = false

                mChart.axisLeft.setDrawGridLines(false)
                mChart.axisLeft.setDrawLabels(false)
                mChart.axisLeft.setDrawAxisLine(false)

                mChart.xAxis.setDrawGridLines(false)
                //mChart.xAxis.setDrawLabels(false)
                mChart.xAxis.textSize = 14F
                mChart.setExtraOffsets(30f, 0f, 0f, 0f)
                mChart.xAxis.setDrawAxisLine(false)
                mChart.xAxis.position = XAxisPosition.BOTTOM

                mChart.axisRight.setDrawGridLines(false)
                mChart.axisRight.setDrawLabels(false)
                mChart.axisRight.setDrawAxisLine(false)


                mChart.animateX(2000)


                bind.barChartRef.invalidate()
                Log.i("jsonRefBind", bind.barChartRef.toString())

            }
        })




        return bind.root
    }


}