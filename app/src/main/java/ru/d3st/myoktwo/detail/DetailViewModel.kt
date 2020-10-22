package ru.d3st.myoktwo.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.d3st.myoktwo.network.MyGroup
import ru.d3st.myoktwo.network.OkMyApi
import ru.ok.android.sdk.OkListener
import kotlin.math.sign


class DetailViewModel(selectedMyGroup: MyGroup, application: Application) : ViewModel() {

    private val _groupData = MutableLiveData<MyGroup>()
    val groupData: LiveData<MyGroup>
        get() = _groupData

    //для гистограмм
    private val _barGraph = MutableLiveData<BarData>()
    val barGraph: LiveData<BarData>
        get() = _barGraph

    val groupId = selectedMyGroup.groupId


    init {
        setGroupData(selectedMyGroup)
        execute()
    }

    private fun setGroupData(selectedMyGroup: MyGroup) {
        _groupData.value = selectedMyGroup
    }

    private fun execute() {
        viewModelScope.launch {
            OkMyApi.getStatPeople(groupId, object : OkListener {
                override fun onError(error: String?) {
                    TODO("Not yet implemented")
                }

                override fun onSuccess(json: JSONObject) {
                    val barGraphData = getGraphData(json.toString())
                    postExecute(barGraphData)
                }
            })
            //_groupData.value = mos.references.toString()

        }
    }

    private fun postExecute(barGraphData: BarData) {
        //Log.i("jsonBarPost", _barGraph.value.toString())


    }


    private fun getGraphData(json: String): BarData {

        val moshi = OkMyApi.adapterGroupPeopleStat.fromJson(json)


        if (moshi != null) {
            Log.i("jsonRef", moshi.references.toString())
/*              Reference(name=FEED, value=85, percentage=54.5),
                Reference(name=SEARCH, value=1, percentage=0.7),
                Reference(name=RECOMMENDATION, value=16, percentage=10.3),
                Reference(name=WIDGET, value=0, percentage=0.0),
                Reference(name=EXTERNAL, value=5, percentage=3.2),
                Reference(name=CATALOG, value=0, percentage=0.0),
                Reference(name=INVITE, value=0, percentage=0.0),
                Reference(name=DISCUSSIONS, value=40, percentage=25.6),
                Reference(name=MY_GROUPS, value=9, percentage=5.7)*/

            //set к гистограмме
            val listData = ArrayList<BarEntry>() //ось X сами графики
            val axisX = ArrayList<String>() //ось Y описание граф

            for ((x, reference) in moshi.references.withIndex()) {

                listData.add(BarEntry(reference.percentage.toFloat(), x))
                axisX.add(reference.name)
            }
            //создаем набор и добавляем в него наши данные
            val barDataSet = BarDataSet(listData, "Reference to Group")
            barDataSet.barSpacePercent = 10f


            //настройки самого Бара
            val dataBarGraph = BarData(axisX, barDataSet)
            dataBarGraph.setValueTextSize(14f)
            _barGraph.value = dataBarGraph

            Log.i("jsonBar3", dataBarGraph.toString())
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
            barDataSet.barSpacePercent
            //chart.setData(data)
            return dataBarGraph


/*            val NoOfEmp = ArrayList<BarEntry>()

            NoOfEmp.add(BarEntry(945f, 0))
            NoOfEmp.add(BarEntry(1040f, 1))
            NoOfEmp.add(BarEntry(1133f, 2))
            NoOfEmp.add(BarEntry(1240f, 3))
            NoOfEmp.add(BarEntry(1369f, 4))
            NoOfEmp.add(BarEntry(1487f, 5))
            NoOfEmp.add(BarEntry(1501f, 6))
            NoOfEmp.add(BarEntry(1645f, 7))
            NoOfEmp.add(BarEntry(1578f, 8))
            NoOfEmp.add(BarEntry(1695f, 9))*/


/*            val year = ArrayList<String>()
            year.add("2008")
            year.add("2009")
            year.add("2010")
            year.add("2011")
            year.add("2012")
            year.add("2013")
            year.add("2014")
            year.add("2015")
            year.add("2016")
            year.add("2017")*/
            //chart.animateY(5000)


        }
        return BarData(emptyList(), BarDataSet(emptyList(), "zero"))

    }
}


