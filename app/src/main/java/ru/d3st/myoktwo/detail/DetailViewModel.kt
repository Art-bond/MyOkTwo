package ru.d3st.myoktwo.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.d3st.myoktwo.network.MyGroup
import ru.d3st.myoktwo.network.OkMyApi

class DetailViewModel(selectedMyGroup: MyGroup, application: Application) : ViewModel() {

    private val _groupData = MutableLiveData<String>()
    val groupData:LiveData<String>
    get() = _groupData

    val groupId = selectedMyGroup.groupId


    init {
        execute()
    }

    private fun execute() {
        viewModelScope.launch{
            val json = OkMyApi.getStatPeople(groupId)
            postexecute(json)

        }
    }

    private fun postexecute(json: String) {

        val moshi = OkMyApi.adapterGroupPeopleStat.fromJson(json)

        if (moshi != null) {
            _groupData.value= moshi.references.toString()
        }
    }
}