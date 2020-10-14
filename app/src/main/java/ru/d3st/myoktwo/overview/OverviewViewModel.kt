package ru.d3st.myoktwo.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.d3st.myoktwo.network.*
import ru.d3st.myoktwo.network.OkMyApi.adapterGroupStatsMoshi
import ru.d3st.myoktwo.network.OkMyApi.getGroupInfo
import ru.d3st.myoktwo.network.OkMyApi.getGroupStatToday
import ru.d3st.myoktwo.network.OkMyApi.getStatTopics
import ru.d3st.myoktwo.network.OkMyApi.ok
import ru.ok.android.sdk.OkRequestMode

import java.lang.Exception

class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    //Сбор информации для заполнения полей листа групп пользоватея
    private val _groupOne = MutableLiveData<List<MyGroup>>()
    val groupOne: LiveData<List<MyGroup>>
        get() = _groupOne

    private val _navigateToSelectedProperty = MutableLiveData<MyGroup>()
    val navigateToSelectedProperty: LiveData<MyGroup>
        get() = _navigateToSelectedProperty



    init {
        execute()
    }
    fun execute() = viewModelScope.launch {
        onPreExecute()
        val result = doInBackground() // runs in background thread without blocking the Main Thread
        Log.e("json1", result)
        val idGroups: List<GroupUser.Group> = getListGroup(result)
        Log.e("json2", idGroups.toString())
        val fillList: List<MyGroup> = getMyGroupList(idGroups)
        Log.e("json3", fillList.toString())
        onPostExecute(fillList)
    }

    private suspend fun getMyGroupList(idGroups: List<GroupUser.Group>): List<MyGroup> =
        withContext(Dispatchers.IO){
            var resultList:List<MyGroup> = emptyList()
            idGroups.forEach {
                val id = it.groupId
                val stats: GroupStats = getGroupStatToday(id)
                val info:GroupInfoItem = getGroupInfo(it)
                val posts:GroupPosts = getStatTopics(id)
                val postCountToday = posts.topics.size

                val name = info.name
                val membersCount = info.membersCount
                val picAvatar = info.picAvatar
                val listMemberDiff = stats.membersDiff
                var membersDiffToday:Int? = 0
                if (listMemberDiff.isNotEmpty()){
                    membersDiffToday = stats.membersDiff.first()?.value
                }

               // val postToday =stats.topicOpens.first().value

                val one =
                    MyGroup(id, name, membersCount, picAvatar, membersDiffToday, postCountToday)
                resultList = resultList.orEmpty() + one
                Log.i("jsonstats", one.toString())

            }
            return@withContext resultList

        }


    private suspend fun getListGroup(json: String): List<GroupUser.Group> =
        withContext(Dispatchers.IO) {
            val list = OkMyApi.adapterGroupMoshi.fromJson(json)
            if (list != null) {
                return@withContext list.groups
            }
            return@withContext emptyList()
        }

    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
    }

    private suspend fun doInBackground(): String =
        withContext(Dispatchers.IO) {    // to run code in Background Thread
            try {
                val result = OkMyApi.ok.request("group.getUserGroupsV2",
                    OkMyApi.mapCountGroup,
                    OkRequestMode.DEFAULT)
                return@withContext result.toString()
            } catch (e: Exception) {
                Log.e("jsonGroups", e.toString())
            }

            return@withContext "error group response"
        }

    // Runs on the Main(UI) Thread
    private fun onPostExecute(result: List<MyGroup>) {
        // hide progress
        _groupOne.value = result
        Log.i("jsonResult",_groupOne.value.toString())
    }

    fun displaySelectedGroup(group:MyGroup){
        _navigateToSelectedProperty.value = group
    }
    fun displaySelectedGroupComplete(){
        _navigateToSelectedProperty.value = null
    }

}