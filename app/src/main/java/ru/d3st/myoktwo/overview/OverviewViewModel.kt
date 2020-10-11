package ru.d3st.myoktwo.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.d3st.myoktwo.network.*
import ru.d3st.myoktwo.network.OkMyApi.adapterGroupStatsMoshi
import ru.d3st.myoktwo.network.OkMyApi.ok
import ru.ok.android.sdk.OkRequestMode

import java.lang.Exception

class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    //Сбор информации для заполнения полей листа групп пользоватея
    private val _groupOne = MutableLiveData<List<MyGroup>>()
    val groupOne: LiveData<List<MyGroup>>
        get() = _groupOne



    init {
        execute()
    }



    //функция получения данных КОНКРЕТНОЙ группы по ID
    private suspend fun getGroupStatToday(groupId: String): GroupStats =
        withContext(Dispatchers.IO ) {
            //cутки
            val dayLongTime = 86400 * 1000
            //вчера
            val start_time: Long = System.currentTimeMillis() - dayLongTime
            //сейчас
            val end_time: Long = System.currentTimeMillis()

            val mapGroupStat = mapOf(
                "gid" to groupId,
                "start_time" to start_time.toString(),
                "end_time" to end_time.toString(),
                "fields" to "members_count, members_diff, topic_opens"
            )

                val json = ok.request("group.getStatTrends",mapGroupStat,OkRequestMode.DEFAULT)
                Log.i("jsonStats", "для группы $groupId - $json")
                val jsonMoshi = adapterGroupStatsMoshi.fromJson(json.toString())

            if (jsonMoshi != null) {
                return@withContext jsonMoshi
            }
            return@withContext emptyList<GroupStats>().first()
        }

    //получение информации о списке групп
    private suspend fun getGroupInfo(oneList: GroupUser.Group): GroupInfoItem =
        withContext(Dispatchers.IO) {
            //вынимаем данные id групп из общего листа с данными групп

            //переводим массив в строку через запятую
            val mapGroupInfo = mapOf(
                "uids" to oneList.groupId,
                "fields" to "NAME, PIC_AVATAR, MEMBERS_COUNT"
            )
            val methodGroupGetInfo = "group.getInfo"

            val json = ok.request(methodGroupGetInfo, mapGroupInfo, OkRequestMode.DEFAULT)
            Log.i("jsonstats", json.toString())

            val moshiResult = OkMyApi.adapterGroupInfo.fromJson(json.toString())

            if (moshiResult != null) {
                return@withContext moshiResult.first()
            }
            return@withContext emptyList<GroupInfoItem>().first()
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
                val stats:GroupStats = getGroupStatToday(id)
                val info:GroupInfoItem = getGroupInfo(it)
                val name = info.name
                val membersCount = info.membersCount
                val picAvatar = info.picAvatar
                val membersDiffToday = stats.membersDiff.first().value
                val postToday =stats.topicOpens.first().value

                val one =
                    MyGroup(id, name, membersCount, picAvatar, membersDiffToday, postToday)
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

}